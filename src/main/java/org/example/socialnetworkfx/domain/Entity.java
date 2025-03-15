package org.example.socialnetworkfx.domain;

public class Entity<ID> {
    private ID identityKey;

    public Entity(ID id) {
        this.identityKey = id;
    }

    public Entity() {
    }

    public ID getID() {
        return identityKey;
    }

    public void setID(ID identityKey) {
        this.identityKey = identityKey;
    }
}
