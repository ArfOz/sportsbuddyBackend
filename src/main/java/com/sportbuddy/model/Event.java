package com.sportbuddy.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import com.sportbuddy.enums.SportEnum;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "events")
public class Event {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Enumerated(EnumType.STRING)
    private SportEnum sportEnum;

    private Double locationLat;
    private Double locationLng;

    private LocalDateTime date;

    private Integer maxParticipants;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;


    @ManyToMany
    @JoinTable(
            name = "event_participants",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> participants;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getLocationLat() {
        return locationLat;
    }

    public void setLocationLat(Double locationLat) {
        this.locationLat = locationLat;
    }

    public SportEnum getSportType() {
        return sportEnum;
    }

    public void setSportType(SportEnum sportEnum) {
        this.sportEnum = sportEnum;
    }

    public Double getLocationLng() {
        return locationLng;
    }

    public void setLocationLng(Double locationLng) {
        this.locationLng = locationLng;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


}
