import com.satori.rtm.*;
import com.satori.rtm.model.AnyJson;
import com.satori.rtm.model.SubscriptionData;

import java.util.Deque;
import java.util.LinkedList;


public class main {
    private static final String endpoint = "wss://open-data.api.satori.com";
    private static final String appkey = "93EFeeb632BF460CCa2dA16fd65cC5f7";
    private static final String channel = "github-events";
    private static ReceiverThread receiver = new ReceiverThread();
    private static RemoverThread eraser = new RemoverThread();
    private static Deque<Event> eventHistory = new LinkedList<Event>();
    private static StatsMap prev1Min = new StatsMap();
    private static StatsMap prev2Min = new StatsMap();
    private static StatsMap prev3Min = new StatsMap();

    public static void main(String[] args) {
        receiver.start();
        eraser.start();

    }

    private static class ReceiverThread extends Thread {

        @Override
        public void run() {
            final RtmClient client = new RtmClientBuilder(endpoint, appkey)
                    .setListener(new RtmClientAdapter() {
                        @Override
                        public void onEnterConnected(RtmClient client) {
                            System.out.println("Connected to Satori RTM!");
                        }
                    })
                    .build();
            SubscriptionAdapter listener = new SubscriptionAdapter() {
                @Override
                public void onSubscriptionData(SubscriptionData data) {
                    for (AnyJson json : data.getMessages()) {
                        Event newEvent = json.convertToType(Event.class);
                        eventHistory.addLast(newEvent);
                        prev1Min.add(newEvent.repo.name);
                        prev2Min.add(newEvent.repo.name);
                        prev3Min.add(newEvent.repo.name);
                    }
                }
            };
            client.createSubscription(channel, SubscriptionMode.SIMPLE, listener);
            client.start();
        }
    }

    private static class RemoverThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(700);
                } catch (Exception e) {}
                boolean flag = true;
                while (flag) {
                    Event e = eventHistory.getFirst();
                    if (e.isNMinOld(3)) {
                        prev3Min.remove(eventHistory.getFirst().repo.name);
                        eventHistory.removeFirst();
                    } else if (e.isNMinOld(2))
                        prev2Min.remove(eventHistory.getFirst().repo.name);
                    else if (e.isNMinOld(1))
                        prev1Min.remove(eventHistory.getFirst().repo.name);
                    else
                        flag = false;
                }
            }
        }
    }

}
//Saves and Deletes.