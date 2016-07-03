package pl.szafraniec.ChildrenMotivator;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import pl.szafraniec.ChildrenMotivator.model.BehaviorTable;
import pl.szafraniec.ChildrenMotivator.model.ChildrenGroup;
import pl.szafraniec.ChildrenMotivator.repository.ChildrenGroupRepository;

import java.util.List;
import java.util.UUID;

public class Program {

    private static final String PERSISTENCE_UNIT_NAME = "ChildrenMotivator";
    //private static EntityManagerFactory FACTORY = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);

    public static void main(String[] args) {
        // open/read the application context file
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("META-INF/applicationContext.xml");

        ChildrenGroupRepository childrenGroupRepository = ctx.getBean(ChildrenGroupRepository.class);
        List<ChildrenGroup> aa = childrenGroupRepository.findAll();
        for (ChildrenGroup a : aa) {
            System.out.println(a.getName() + a.getBehaviorTable());
        }
        ChildrenGroup cg = new ChildrenGroup();
        cg.setName(UUID.randomUUID().toString());
        cg.setBehaviorTable(new BehaviorTable());
        childrenGroupRepository.save(cg);

        //EntityManager em = FACTORY.createEntityManager();
        // Read the existing entries and write to console
        //        Query q = em.createQuery("select t from Todo t");
        //        List<Todo> todoList = q.getResultList();
        //        for (Todo todo : todoList) {
        //            System.out.println(todo);
        //        }
        //        System.out.println("Size: " + todoList.size());

        // Create new todo
        //        em.getTransaction().begin();
        //        //        em.persist(todo);
        //        em.getTransaction().commit();
        //
        //        em.close();
    }
}