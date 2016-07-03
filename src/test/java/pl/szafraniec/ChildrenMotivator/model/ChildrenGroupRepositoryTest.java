package pl.szafraniec.ChildrenMotivator.model;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.dbunit.dataset.DataSetException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import pl.szafraniec.ChildrenMotivator.model.annotation.DaoTest;
import pl.szafraniec.ChildrenMotivator.repository.ChildRepository;
import pl.szafraniec.ChildrenMotivator.repository.ChildrenGroupRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@DaoTest
@RunWith(SpringJUnit4ClassRunner.class)
public class ChildrenGroupRepositoryTest {

    @Autowired
    private ChildrenGroupRepository childrenGroupRepository;

    @Autowired
    private ChildRepository childRepository;

    @Test
    @DatabaseSetup({ "classpath:data/ChildrenGroup/read.xml" })
    public void readTest() throws SQLException, DataSetException {
        Assert.assertEquals(2, childrenGroupRepository.count());
        ChildrenGroup first = childrenGroupRepository.findOne(1);
        Assert.assertEquals("name1", first.getName());
        ChildrenGroup second = childrenGroupRepository.findOne(2);
        Assert.assertEquals("name2", second.getName());
        Assert.assertEquals(2, second.getChildren().size());
    }

    @Test
    public void saveTest() {
        ChildrenGroup childrenGroup = new ChildrenGroup();
        childrenGroup.setName(UUID.randomUUID().toString());
        childrenGroup.setBehaviorTable(new BehaviorTable());
        childrenGroupRepository.saveAndFlush(childrenGroup);
        List<ChildrenGroup> all = childrenGroupRepository.findAll();
        Assert.assertEquals(1, all.size());
        Assert.assertEquals(all.get(0).getName(), childrenGroup.getName());
    }

    @Test
    public void saveWithChildrenTest() {
        int childrenCount = 10;
        ChildrenGroup childrenGroup = new ChildrenGroup();
        childrenGroup.setName(UUID.randomUUID().toString());
        childrenGroup.setBehaviorTable(new BehaviorTable());
        List<Child> children = IntStream.range(0, childrenCount).mapToObj(i -> {
            Child child = new Child();
            child.setName("name" + i);
            child.setSurname("surname" + i);
            child.setPesel("pesel" + i);
            child.setChildrenGroup(childrenGroup);
            return child;
        }).collect(Collectors.toList());
        childrenGroup.setChildren(children);
        childrenGroupRepository.saveAndFlush(childrenGroup);
        List<ChildrenGroup> all = childrenGroupRepository.findAll();
        Assert.assertEquals(1, all.size());
        ChildrenGroup group = all.get(0);
        Assert.assertEquals(group.getName(), childrenGroup.getName());
        Assert.assertEquals(childrenCount, group.getChildren().size());
        IntStream.range(0, childrenCount).forEach(i -> {
            Child originalChild = children.get(i);
            Child dbChild = group.getChildren().get(i);
            Assert.assertEquals(group.getId(), dbChild.getChildrenGroup().getId());
            Assert.assertEquals(originalChild.getName(), dbChild.getName());
            Assert.assertEquals(originalChild.getSurname(), dbChild.getSurname());
            Assert.assertEquals(originalChild.getPesel(), dbChild.getPesel());
        });
    }

    @Test
    @DatabaseSetup(value = { "classpath:data/ChildrenGroup/read.xml" })
    public void updateNameTest() {
        int id = 1;
        String newName = "new_name";
        ChildrenGroup group = childrenGroupRepository.findOne(id);
        group.setName(newName);
        childrenGroupRepository.saveAndFlush(group);

        group = childrenGroupRepository.findOne(id);

        Assert.assertEquals(newName, group.getName());
    }

    @Test
    @DatabaseSetup(value = { "classpath:data/ChildrenGroup/read.xml" })
    public void updateChildrenTest() {
        int id = 1;
        ChildrenGroup group = childrenGroupRepository.findOne(id);
        int previousSize = group.getChildren().size();
        Child child = new Child();
        child.setName("name");
        child.setSurname("surname");
        child.setPesel("pesel");
        child.setChildrenGroup(group);
        group.getChildren().add(child);
        childrenGroupRepository.saveAndFlush(group);

        group = childrenGroupRepository.findOne(id);
        Assert.assertEquals(previousSize + 1, group.getChildren().size());
    }

    @Test
    @DatabaseSetup(value = { "classpath:data/ChildrenGroup/read.xml" })
    public void deleteEmptyGroupTest() {
        childrenGroupRepository.delete(1);
        childrenGroupRepository.flush();
        Assert.assertEquals(1, childrenGroupRepository.count());
    }

    @Test(expected = JpaSystemException.class)
    @DatabaseSetup(value = { "classpath:data/ChildrenGroup/read.xml" })
    public void impossibleDeleteGroupWithChildrenTest() {
        childrenGroupRepository.delete(2);
        childrenGroupRepository.flush();
    }
}
