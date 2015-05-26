package models;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class KunderaExample
{
    public static void main(String[] args)
    {
        User2 user = new User2( "John", "Smith" ,"London");
        user.setUserId("0001");

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("cassandra_pu");
        EntityManager em = emf.createEntityManager();

        em.persist(user);
        em.close();    
        emf.close();
        
        System.out.println("Query finished");
    }
}