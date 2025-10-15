package com.frankmoley.lil.data.entity;

import java.sql.Timestamp;
import com.frankmoley.lil.data.entity.NoteDraft;

public class Note extends NoteDraft {
  private long id;
  private Timestamp createdAt;
  private Timestamp updatedAt;

  public Note() {
    super();
  }

  public Note(String title, String content) {
    super(title, content);
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public Timestamp getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Timestamp createdAt) {
    this.createdAt = createdAt;
  }

  public Timestamp getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Timestamp updatedAt) {
    this.updatedAt = updatedAt;
  }

  @Override
  public String toString() {
    return "Note [id=" + id + ", title=" + getTitle() + ", content=" + getContent() +
            ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
  }
}
