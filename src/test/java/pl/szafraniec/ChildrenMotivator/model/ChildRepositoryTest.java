package pl.szafraniec.ChildrenMotivator.model;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.dbunit.dataset.DataSetException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import pl.szafraniec.ChildrenMotivator.model.annotation.DaoTest;
import pl.szafraniec.ChildrenMotivator.repository.ChildRepository;
import pl.szafraniec.ChildrenMotivator.repository.ChildrenGroupRepository;

import java.sql.SQLException;
import java.util.stream.IntStream;

@DaoTest
@RunWith(SpringJUnit4ClassRunner.class)
public class ChildRepositoryTest {

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private ChildrenGroupRepository childrenGroupRepository;

    @Test
    @DatabaseSetup(value = { "classpath:data/Child.xml" })
    public void readTest() throws SQLException, DataSetException {
        Assert.assertEquals(3, childRepository.count());
        IntStream.rangeClosed(1, 3).forEach(i -> {
            Child iChild = childRepository.getOne(i);
            Assert.assertEquals("name" + i, iChild.getName());
            Assert.assertEquals("surname" + i, iChild.getSurname());
            Assert.assertEquals(Integer.toString(i), iChild.getPesel());
            Assert.assertEquals(1, iChild.getChildrenGroup().getId());
        });
    }

    @Test
    @DatabaseSetup(value = { "classpath:data/Child.xml" })
    public void addTest() {
        int groupId = 2;
        ChildrenGroup childrenGroup = childrenGroupRepository.getOne(groupId);
        int originalSize = childrenGroup.getChildren().size();
        Child child = new Child();
        child.setName("name");
        child.setSurname("surname");
        child.setPesel("pesel");
        child.setChildrenGroup(childrenGroup);
        childrenGroup.getChildren().add(child);

        childrenGroupRepository.saveAndFlush(childrenGroup);

        childrenGroup = childrenGroupRepository.getOne(groupId);
        Child savedChild = childrenGroup.getChildren().get(originalSize);
        Assert.assertEquals(originalSize + 1, childrenGroup.getChildren().size());
        Assert.assertEquals(child.getName(), savedChild.getName());
        Assert.assertEquals(child.getSurname(), savedChild.getSurname());
        Assert.assertEquals(child.getPesel(), savedChild.getPesel());
    }

    @Test
    @DatabaseSetup(value = { "classpath:data/Child.xml" })
    public void updateNameTest() {
        int id = 1;
        String newName = "new_name";
        Child child = childRepository.findOne(id);
        child.setName(newName);
        childRepository.saveAndFlush(child);

        child = childRepository.findOne(id);

        Assert.assertEquals(newName, child.getName());
    }

    @Test
    @DatabaseSetup(value = { "classpath:data/Child.xml" })
    public void changeGroupTest() {
        ChildrenGroup first = childrenGroupRepository.findOne(1);
        ChildrenGroup second = childrenGroupRepository.findOne(2);
        Child movedChild = first.getChildren().get(0);
        int firstPreviousSize = first.getChildren().size();
        int secondPreviousSize = second.getChildren().size();

        movedChild.setChildrenGroup(second);
        second.getChildren().add(movedChild);
        first.getChildren().remove(movedChild);
        childrenGroupRepository.save(first);
        childrenGroupRepository.saveAndFlush(second);

        first = childrenGroupRepository.findOne(1);
        second = childrenGroupRepository.findOne(2);
        Child savedChild = childRepository.findOne(movedChild.getId());
        Assert.assertEquals(firstPreviousSize - 1, first.getChildren().size());
        Assert.assertEquals(secondPreviousSize + 1, second.getChildren().size());
        Assert.assertEquals(movedChild.getName(), savedChild.getName());
        Assert.assertEquals(movedChild.getSurname(), savedChild.getSurname());
        Assert.assertEquals(movedChild.getPesel(), savedChild.getPesel());
        Assert.assertEquals(2, savedChild.getChildrenGroup().getId());
    }

    @Test
    @DatabaseSetup(value = { "classpath:data/Child.xml" })
    public void deleteTest() {
        long childCount = childRepository.count();
        ChildrenGroup childrenGroup = childrenGroupRepository.getOne(1);
        int groupSize = childrenGroup.getChildren().size();
        Child child = childrenGroup.getChildren().get(0);
        childrenGroup.getChildren().remove(child);
        childRepository.delete(child);
        childrenGroupRepository.flush();
        Assert.assertEquals(childCount - 1, childRepository.count());
        Assert.assertEquals(groupSize - 1, childrenGroupRepository.getOne(1).getChildren().size());

    }
}
