package org.example.DataBase;


import jakarta.persistence.*;


@Entity
@Table(name = "players")
public class Players
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true, nullable = false)
    private String playerName;
    private Integer score;

    public Players()
    {
    }
    public Players(String playerName, Integer score)
    {
        this.playerName = playerName;
        this.score = score;
    }
    public Players(String playerName)
    {
        this.playerName = playerName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return playerName;
    }

    public void setName(String name) {
        this.playerName = name;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}