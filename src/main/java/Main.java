import com.satori.rtm.*;
import com.satori.rtm.model.AnyJson;
import com.satori.rtm.model.SubscriptionData;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;


public class Main {
    private static final String endpoint = "wss://open-data.api.satori.com";
    private static final String appkey = "93EFeeb632BF460CCa2dA16fd65cC5f7";
    private static final String channel = "github-events";
    private static ReceiverThread receiver = new ReceiverThread();
    private static RemoverThread eraser = new RemoverThread();
    private static outputThread outputManager = new outputThread();
    private static ConcurrentLinkedQueue<Event> eventHistory = new ConcurrentLinkedQueue<Event>();
    private static StatsMap prev1Min = new StatsMap("1Min");
    private static StatsMap prev2Min = new StatsMap("2Min");
    private static StatsMap prev3Min = new StatsMap("3Min");

    public static void main(String[] args) {
        receiver.start();
        eraser.start();
        outputManager.start();
    }

    private static class ReceiverThread extends Thread {

        @Override
        public void run() {
            final RtmClient client = new RtmClientBuilder(endpoint, appkey)
                    .setListener(new RtmClientAdapter() {
                        @Override
                        public void onEnterConnected(RtmClient client) {
                            System.out.println("Connected to Satori RTM! :)");
                        }
                    })
                    .build();
            SubscriptionAdapter listener = new SubscriptionAdapter() {
                @Override
                public void onSubscriptionData(SubscriptionData data) {
                    for (AnyJson json : data.getMessages()) {
                        Event newEvent = json.convertToType(Event.class);
                        eventHistory.add(newEvent);
                        prev1Min.add(newEvent.repo);
                        prev2Min.add(newEvent.repo);
//                        System.out.println(newEvent);
                        prev3Min.add(newEvent.repo);
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
            while (eventHistory.isEmpty())
                try {
                    Thread.sleep(450);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            while (true) {
                try {
                    Thread.sleep(700);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Iterator<Event> iterator = eventHistory.iterator();

                boolean flag = true;
                while (flag) {
                    Event e = iterator.next();
                    if (e.isNMinOld(3)) {
                        prev3Min.remove(e.repo);
                        eventHistory.poll();
                        iterator = eventHistory.iterator();
                    } else if (e.isNMinOld(2))
                        prev2Min.remove(e.repo);
                    else if (e.isNMinOld(1))
                        prev1Min.remove(e.repo);
                    else
                        flag = false;
                }
            }
        }
    }

    private static class outputThread extends Thread {
        @Override
        public void run() {
            PrintStream outputStream = System.out;
            while (true) {
                try {
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("output:");
                StringBuilder output = new StringBuilder();
                output.append(prev1Min.toString());
                output.append(prev2Min.toString());
                output.append(prev3Min.toString());
                outputStream.printf("%s\n", output.toString());
            }
        }
    }

}
//Saves and Deletes.