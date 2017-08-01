//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Iterator;
//
//public class Test {
//    static HashMap<User, Long> userData = new HashMap<User, Long>();
//
//    public static void main(String[] args) {
//        for (long i = 0; i < 50; i++) {
//            userData.put(new User("u" + i), i);
//        }
//
//        System.out.println(userOutput());
//
//    }
//
//
//    private static String userOutput() {
//        ArrayList<UserStat> list = new ArrayList<UserStat>(11);
//        Iterator<User> iterator = userData.keySet().iterator();
//        for (int i = 0; i < 11 && iterator.hasNext(); i++) {
//            User r = iterator.next();
//            list.add(new UserStat(r, userData.get(r)));
//        }
//        Collections.sort(list);
//        while (iterator.hasNext()) {
//            User r = iterator.next();
//            list.add(new UserStat(r, userData.get(r)));
//            Collections.sort(list);
//        }
//        StringBuilder result = new StringBuilder("{");
//        for (int i = 0; i < 10; i++) {
//            if (i >= list.size()) break;
//            UserStat t = list.get(i);
//            result.append(t.toString());
//            result.append(" , ");
//        }
//        result.append("}\n");
//
//        return result.toString();
//    }
//
//}
//
