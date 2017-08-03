import com.satori.rtm.*;
import com.satori.rtm.model.AnyJson;
import com.satori.rtm.model.SubscriptionData;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

import java.io.File;
import java.io.PrintStream;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;


public class Main {
    private static final String endpoint = "wss://open-data.api.satori.com";
    private static final String appkey = "93EFeeb632BF460CCa2dA16fd65cC5f7";
    private static final String channel = "github-events";
    private static ReceiverThread receiver = new ReceiverThread();
    private static RemoverThread eraser = new RemoverThread();
    private static outputThread outputManager = new outputThread();
    private static StatsMap prev1Min = new StatsMap("1Min");
    private static StatsMap prev2Min = new StatsMap("2Min");
    private static StatsMap prev3Min = new StatsMap("3Min");
    private static DB db;
    private static HTreeMap eventHistory;
    private static File DBFile = new File("DBs/mapDB.db");

    public static void main(String[] args) {
        DBMaker.Maker maker = DBMaker
                .fileDB(DBFile)
                .closeOnJvmShutdown()
                .transactionEnable()
                .fileMmapEnableIfSupported()
                .allocateStartSize(512 * 1024 * 1024)     // 512MB
                .allocateIncrement(256 * 1024 * 1024);     // 256MB;
        try {
            db = maker.make();
        } catch (Exception e) {
            try {
                DBFile.createNewFile();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            db = maker.make();
        }
        eventHistory = db
                .hashMap("EventQueue")
                .keySerializer(Serializer.LONG)
                .expireAfterCreate(24, TimeUnit.HOURS)
                .layout(16, 32, 8)
                .createOrOpen();
        if (!eventHistory.isEmpty()) {
            Iterator<Long> iterator = eventHistory.keySet().iterator();
            while (iterator.hasNext()) {
                long t = iterator.next();
                Event e = (Event) eventHistory.get(t);
                if (e.isNMinOld(3)) {
                    long tmp = t;
                    if (iterator.hasNext())
                        t = iterator.next();
                    eventHistory.remove(tmp);
                } else break;
            }
            db.commit();
            iterator = eventHistory.keySet().iterator();
            while (iterator.hasNext()) {
                Object t = iterator.next();
                Event e = (Event) eventHistory.get(t);
                if (!e.isNMinOld(1)) {
                    prev3Min.add(e);
                    prev2Min.add(e);
                    prev1Min.add(e);
                } else if (!e.isNMinOld(2)) {
                    prev3Min.add(e);
                    prev2Min.add(e);
                } else if (!e.isNMinOld(3)) {
                    prev3Min.add(e);
                } else {
                    break;
                }
            }
        }
        outputManager.start();
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
                            eventHistory.put(newEvent.instanciationTime, newEvent);
                            db.commit();
                            prev1Min.add(newEvent);
                            prev2Min.add(newEvent);
                            prev3Min.add(newEvent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
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

                Iterator<Long> iterator = eventHistory.keySet().iterator();

                boolean flag = true;
                while (flag && iterator.hasNext()) {
                    long t = iterator.next();
                    Event e = (Event) eventHistory.get(t);
                    if (e.isNMinOld(3)) {
                        prev3Min.remove(e);
                        long tmp = t;
                        t = iterator.next();
                        eventHistory.remove(tmp);
                    } else if (e.isNMinOld(2)) {
                        prev2Min.remove(e);
                    } else if (e.isNMinOld(1)) {
                        prev1Min.remove(e);
                    } else {
                        flag = false;
                    }
                }
                db.commit();
            }
        }
    }

    private static class outputThread extends Thread {
        @Override
        public void run() {
            PrintStream outputStream = System.out;
            while (true) {
                System.out.println("output@ " + new Date().toString() + " : ");
                StringBuilder output = new StringBuilder();
                output.append(prev1Min.toString());
                output.append("\n");
                output.append(prev2Min.toString());
                output.append("\n");
                output.append(prev3Min.toString());
                output.append("\n");
                outputStream.printf("%s\n", output.toString());
                try {
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}