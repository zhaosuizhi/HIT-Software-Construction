package P3;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.security.KeyException;
import java.util.*;

public class FriendshipGraph {
    private final HashMap<String, Set<String>> graph; // 关系有向图，以学生对象为键

    private static final String existMsgFormat = "Person \"%s\" already exists!";
    private static final String notFoundMsgFormat = "Person \"%s\" not found!";

    public FriendshipGraph() {
        graph = new HashMap<>();
    }

    /**
     * 检查姓名是否在关系图中
     *
     * @param name 姓名
     * @return 是否在关系图中
     */
    private boolean personExist(String name) {
        return graph.get(name) != null;
    }

    /**
     * 检查姓名是否出现在关系图中
     * 若没有，抛出KeyException错误
     *
     * @param name 姓名
     * @throws KeyException 当名字不存在时
     */
    private void existOrThrow(String name) throws KeyException {
        if (!personExist(name)) {
            throw new KeyException(String.format(notFoundMsgFormat, name));
        }
    }

    /**
     * 检查姓名是否出现在关系图中
     * 若存在，抛出KeyAlreadyExistsException错误
     *
     * @param name 姓名
     * @throws KeyAlreadyExistsException 当名字已存在时
     */
    private void notExistOrThrow(String name) throws KeyAlreadyExistsException {
        if (personExist(name)) {
            throw new KeyAlreadyExistsException(String.format(existMsgFormat, name));
        }
    }

    /**
     * 在关系图中添加一个新人；若存在则抛出KeyAlreadyExistsException
     *
     * @param person 人物对象
     */
    public void addVertex(Person person) {
        String name = person.getName();
        notExistOrThrow(name);
        graph.put(name, new HashSet<>());
    }

    /**
     * 在关系图中添加一个新关系
     * 若人物不存在存在则抛出KeyException
     *
     * @param from 关系的源人物
     * @param to   关系的目标人物
     * @return 若边成功添加则为true；边已存在则为false
     * @throws KeyException 当from和to的名字有任一不存在时
     */
    public boolean addEdge(Person from, Person to) throws KeyException {
        String fromName = from.getName();
        existOrThrow(fromName);

        String toName = to.getName();
        existOrThrow(toName);

        if (fromName.equals(toName)) // 不能自指
            throw new IllegalArgumentException("Person cannot be add an edge to itself!");

        Set<String> fromSet = graph.get(fromName);
        return fromSet.add(toName);
    }

    /**
     * 查找两人的社交距离
     *
     * @param from 源人物
     * @param to   目标人物
     * @return 二者的社交距离；不相关则返回-1
     * @throws KeyException 当from和to的名字有任一不存在时
     */
    public int getDistance(Person from, Person to) throws KeyException {
        class PersonWithDistance {
            final String name;
            final int distance;

            public PersonWithDistance(String name, int distance) {
                this.name = name;
                this.distance = distance;
            }
        }

        String fromName = from.getName();
        existOrThrow(fromName);

        String toName = to.getName();
        existOrThrow(toName);

        Queue<PersonWithDistance> queue = new LinkedList<>(); // BFS的队列
        Set<String> visit = new HashSet<>(); // 存储已经访问过的人物
        final int personCNT = graph.size(); // 人物总数

        queue.add(new PersonWithDistance(fromName, 0));
        while (!queue.isEmpty() && visit.size() < personCNT) {
            PersonWithDistance top = queue.remove();

            if (top.name.equals(toName))
                return top.distance;

            for (String name : graph.get(top.name)) {
                if (!visit.contains(name)) {
                    queue.add(new PersonWithDistance(name, top.distance + 1));
                    visit.add(name);
                }
            }
        }

        return -1;
    }

    public static void main(String[] args) throws KeyException {
        FriendshipGraph graph = new FriendshipGraph();
        Person rachel = new Person("Rachel");
        Person ross = new Person("Ross");
        Person ben = new Person("Ben");
        Person kramer = new Person("Kramer");

        graph.addVertex(rachel);
        graph.addVertex(ross);
        graph.addVertex(ben);
        graph.addVertex(kramer);

        graph.addEdge(rachel, ross);
        graph.addEdge(ross, rachel);
        graph.addEdge(ross, ben);
        graph.addEdge(ben, ross);

        System.out.println(graph.getDistance(rachel, ross));   //should print 1
        System.out.println(graph.getDistance(rachel, ben));    //should print 2
        System.out.println(graph.getDistance(rachel, rachel)); //should print 0
        System.out.println(graph.getDistance(rachel, kramer)); //should print -1
    }
}
