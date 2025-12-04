package org.example.DataBase;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.List;

public class ManageDataBase
{
    private SessionFactory sessionFactory;
    public ManageDataBase()
    {
        try
        {
            sessionFactory = new Configuration().configure().buildSessionFactory();
        }
        catch(Exception e)
        {
            System.out.println("Coudn't create session: " + e);
            System.exit(1);
        }
    }

    public boolean ifPlayerNameIsFree(String playerName)
    {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        boolean check = false;

        try
        {
            transaction = session.beginTransaction();
            String hql = "Select p from Players p where p.playerName =:name";
            Players player = session.createSelectionQuery(hql, Players.class).setParameter("name", playerName).uniqueResult();
            transaction.commit();
            if(player != null) check = true;
        }
        catch (Exception e)
        {
            System.err.println("Could not check whether Player exists" + e);
            System.exit(5);
        }
        finally {
            session.close();
        }
        return check;
    }
    public void addScore(Players dataBase)
    {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        if(ifPlayerNameIsFree(dataBase.getPlayerName()))
        {
            System.out.println("User name with this Nick exits!");
            return;
        }

        try
        {
            transaction = session.beginTransaction();
            session.persist(dataBase);
            transaction.commit();
        }
        catch (Exception e)
        {
            System.err.println("The user could not be added to database: " + e);
        }
    }

    public List<Players>  getAllScores()
    {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        List<Players> players = null;
        try
        {
            transaction = session.beginTransaction();
            String hql = "Select p.playerName,p.score from Players p ORDER BY p.score DESC ";
            players = session.createQuery(hql, Players.class).getResultList();
            transaction.commit();
        }
        catch(Exception e)
        {
            if (transaction != null) {
                transaction.rollback();
            }
            System.out.println("Cos poszlo nie tak " + e);
        }
        finally {
            session.close();
        }
        return players;
    }
}
