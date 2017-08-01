import com.satori.rtm.*;
import com.satori.rtm.model.AnyJson;
import com.satori.rtm.model.SubscriptionData;

import java.io.PrintStream;
import java.util.Date;
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
    private static StatsMap prev10Min = new StatsMap("10Min");
    private static StatsMap prev20Min = new StatsMap("20Min");
    private static StatsMap prev30Min = new StatsMap("30Min");

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
                    try {
                        for (AnyJson json : data.getMessages()) {
                            Event newEvent = json.convertToType(Event.class);
                            eventHistory.add(newEvent);
                            prev10Min.add(newEvent);
                            prev20Min.add(newEvent);
                            prev30Min.add(newEvent);
                        }
                    } catch (Exception e) { e.printStackTrace(); }
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

            remover:
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
                    if (e == null)
                        continue remover;
                    if (e.isNMinOld(30)) {
                        prev30Min.remove(e);
                        eventHistory.poll();
                        iterator = eventHistory.iterator();
                    } else if (e.isNMinOld(20)) {
                        prev20Min.remove(e);
                    } else if (e.isNMinOld(10)) {
                        prev10Min.remove(e);
                    } else {
                        flag = false;
                    }
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
                System.out.println("output@ " + new Date().toString() + " : ");
                StringBuilder output = new StringBuilder();
                output.append(prev10Min.toString());
                output.append(prev20Min.toString());
                output.append(prev30Min.toString());
                outputStream.printf("%s\n", output.toString());
            }
        }
    }

}
//Saves and Deletes.