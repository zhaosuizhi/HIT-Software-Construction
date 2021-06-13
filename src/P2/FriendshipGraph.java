package P2;

import P1.graph.Graph;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.security.KeyException;
import java.util.*;

public class FriendshipGraph {
    private final Graph<Person> graph;

    private static final String existMsgFormat = "Person \"%s\" already exists!";
    private static final String notFoundMsgFormat = "Person \"%s\" not found!";

    // Abstraction function:
    //   AF(graph) = Directed Graph D = (V, E) refers to a friendship graph
    //       V = graph.vertices()
    //       E = from src to v for src in graph.sources(v).keySet()
    //             unions from v to tar for tar in graph.targets(v).keySet()
    //             for v in V
    // Representation invariant:
    //   1. graph != null
    //   2. for each v in V, v not in neither graph.sources(v).keySet() nor graph.targets(v).keySet(),
    //          which means no self-reference
    //   The 1st one is guaranteed by constructor.
    // Safety from rep exposure:
    //   graph is "private final" so it can't be reassigned
    //   String is immutable

    public FriendshipGraph() {
        graph = Graph.empty();
    }

    private void checkRep() {
        Set<Person> peopleSet = graph.vertices();
        for (Person p : peopleSet) {
            assert !graph.sources(p).containsKey(p);
            assert !graph.targets(p).containsKey(p);
        }
    }

    /**
     * 检查人物是否在关系图中
     *
     * @param person 人物
     * @return 是否在关系图中
     */
    private boolean personExist(Person person) {
        return graph.vertices().contains(person);
    }

    /**
     * 检查人物是否出现在关系图中
     * 若没有，抛出KeyException错误
     *
     * @param person 人物
     * @throws KeyException 当名字不存在时
     */
    private void existOrThrow(Person person) throws KeyException {
        if (!personExist(person)) {
            throw new KeyException(String.format(notFoundMsgFormat, person.getName()));
        }
    }

    /**
     * 检查姓名是否出现在关系图中
     * 若存在，抛出KeyAlreadyExistsException错误
     *
     * @param person 人物
     * @throws KeyAlreadyExistsException 当名字已存在时
     */
    private void notExistOrThrow(Person person) throws KeyAlreadyExistsException {
        if (personExist(person)) {
            throw new KeyAlreadyExistsException(String.format(existMsgFormat, person.getName()));
        }
    }

    /**
     * 在关系图中添加一个新人；若存在则抛出KeyAlreadyExistsException
     *
     * @param person 人物
     */
    public void addVertex(Person person) {
        notExistOrThrow(person);
        graph.add(person);
        checkRep();
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
        existOrThrow(from);
        existOrThrow(to);

        if (from.equals(to)) // 不能自指
            throw new IllegalArgumentException("Person cannot be add an edge to itself!");

        int setResult = graph.set(from, to, 1);
        checkRep();
        return setResult == 0;
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
            final Person person;
            final int distance;

            public PersonWithDistance(Person person, int distance) {
                this.person = person;
                this.distance = distance;
            }
        }

        existOrThrow(from);
        existOrThrow(to);

        Queue<PersonWithDistance> queue = new LinkedList<>(); // BFS的队列
        Set<Person> visit = new HashSet<>(); // 存储已经访问过的人物
        final int personCNT = graph.vertices().size(); // 人物总数

        queue.add(new PersonWithDistance(from, 0));
        while (!queue.isEmpty() && visit.size() < personCNT) {
            PersonWithDistance top = queue.remove();

            if (top.person.equals(to))
                return top.distance;

            for (Person person : graph.targets(top.person).keySet()) {
                if (!visit.contains(person)) {
                    queue.add(new PersonWithDistance(person, top.distance + 1));
                    visit.add(person);
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
