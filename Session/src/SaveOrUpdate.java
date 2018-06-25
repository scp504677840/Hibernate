import entity.HeroEntity;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;

/**
 * hibernate把对象分为四种状态：
 * 1.持久化状态
 * 2.临时状态
 * 3.游离状态
 * 4.删除状态
 */
public class SaveOrUpdate {
    private static final SessionFactory ourSessionFactory;

    static {
        try {
            //使用加载hibernate.cfg.xml方式配置hibernate
            Configuration configuration = new Configuration().configure();
            ourSessionFactory = configuration.buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static Session getSession() throws HibernateException {
        return ourSessionFactory.openSession();
    }

    public static void main(final String[] args) throws Exception {
        final Session session = getSession();
        Transaction transaction = session.beginTransaction();
        try {

            // 临时状态
            // ①通过new的方式创建对象
            // HeroEntity heroEntity = new HeroEntity(Timestamp.from(Instant.now()), Timestamp.from(Instant.now()), "YiNew");

            // ②通过查询数据库的方式获取对象
            HeroEntity heroEntity = session.get(HeroEntity.class, BigInteger.valueOf(3L));
            heroEntity.setName("Er");

            // 持久化状态
            // 保存方式一【推荐】
            session.saveOrUpdate(heroEntity);

            // 保存方式二，其中第一个参数传入entity名称，如果有的话；在实体类@Entity注解里面定义。
            //String entityName = "heroEntity";
            //session.saveOrUpdate(entityName, heroEntity);

            transaction.commit();

        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
            ourSessionFactory.close();
        }
    }
    /*
    对应①的SQL
    Hibernate:
    insert
    into
        hero
        (gmt_create, gmt_modified, name)
    values
        (?, ?, ?)

        对应②的SQL
        Hibernate:
    select
        heroentity0_.id as id1_0_0_,
        heroentity0_.gmt_create as gmt_crea2_0_0_,
        heroentity0_.gmt_modified as gmt_modi3_0_0_,
        heroentity0_.name as name4_0_0_
    from
        hero heroentity0_
    where
        heroentity0_.id=?
Hibernate:
    update
        hero
    set
        gmt_create=?,
        gmt_modified=?,
        name=?
    where
        id=?
     */
}