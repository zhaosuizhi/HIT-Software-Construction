package P3;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.security.KeyException;
import java.util.*;

public class FriendshipGraph {
    private final HashMap<Person, Set<Person>> graph; // 关系有向图，以学生对象为键

    private static final String existMsgFormat = "Person \"%s\" already exists!";
    private static final String notFoundMsgFormat = "Person \"%s\" not found!";

    public FriendshipGraph() {
        graph = new HashMap<>();
    }

    /**
     * 检查人物是否在关系图中
     *
     * @param person 人物
     * @return 是否在关系图中
     */
    private boolean personExist(Person person) {
        return graph.get(person) != null;
    }

    /**
     * 在关系图中添加一个新人；若存在则抛出KeyAlreadyExistsException
     *
     * @param person 人物对象
     */
    public void addVertex(Person person) {
        if (personExist(person)) {
            throw new KeyAlreadyExistsException(String.format(existMsgFormat, person.getName()));
        }
        graph.put(person, new HashSet<>());
    }

    /**
     * 在关系图中添加一个新人；若存在则抛出KeyAlreadyExistsException
     *
     * @param from 关系的源人物
     * @param to   关系的目标人物
     */
    public void addEdge(Person from, Person to) throws KeyException {
        Set<Person> fromSet = graph.get(from);
        if (fromSet == null) {
            throw new KeyException(String.format(notFoundMsgFormat, from.getName()));
        }
        if (!personExist(to)) {
            throw new KeyException(String.format(notFoundMsgFormat, to.getName()));
        }

        fromSet.add(to);
    }

    /**
     * 查找两人的社交距离
     *
     * @param from 源人物
     * @param to   目标人物
     * @return 二者的社交距离；不相关则返回-1
     */
    public int getDistance(Person from, Person to) {
        class PersonWithDistance {
            public final Person person;
            public final int distance;

            public PersonWithDistance(Person person, int distance) {
                this.person = person;
                this.distance = distance;
            }
        }

        Queue<PersonWithDistance> queue = new LinkedList<>(); // BFS的队列
        Set<Person> visit = new HashSet<>(); // 存储已经访问过的人物
        int personCNT = graph.size(); // 人物总数

        queue.add(new PersonWithDistance(from, 0));
        while (!queue.isEmpty() && visit.size() < personCNT) {
            PersonWithDistance top = queue.remove();

            if (top.person == to)
                return top.distance;

            for (Person p : graph.get(top.person)) {
                if (!visit.contains(p)) {
                    queue.add(new PersonWithDistance(p, top.distance + 1));
                    visit.add(p);
                }
            }
        }

        return -1;
    }

    public static void main(String[] args) {
        FriendshipGraph graph = new FriendshipGraph();
        Person rachel = new Person("Rachel");
        Person ross = new Person("Ross");
        Person ben = new Person("Ben");
        Person kramer = new Person("Kramer");

        graph.addVertex(rachel);
        graph.addVertex(ross);
        graph.addVertex(ben);
        graph.addVertex(kramer);

        try {
            graph.addEdge(rachel, ross);
            graph.addEdge(ross, rachel);
            graph.addEdge(ross, ben);
            graph.addEdge(ben, ross);
        } catch (KeyException e) {
            System.out.println(e);
        }

        System.out.println(graph.getDistance(rachel, ross));   //should print 1
        System.out.println(graph.getDistance(rachel, ben));    //should print 2
        System.out.println(graph.getDistance(rachel, rachel)); //should print 0
        System.out.println(graph.getDistance(rachel, kramer)); //should print -1
    }
}
