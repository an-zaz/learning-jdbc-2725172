package com.frankmoley.lil.data.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import com.frankmoley.lil.data.entity.Note;
import com.frankmoley.lil.data.util.DatabaseUtils;

public class NoteDao implements Dao<Note, long>{
  private static final Logger LOGGER = Logger.getLogger(NoteDao.class.getName());
  private static final String GET_ALL = "select * from wisdom.notes";
  private static final String GET_ONE = "select * from wisdom.notes where id = ?";
  private static final String CREATE = "insert into wisdom.notes (title, content) VALUES (?, ?) RETURNING id, title, content, created_at, updated_at";
  private static final String DELETE = "delete from wisdom.notes where id = ?";
  private static final String GET_ALL_PAGED = "select id, title, content, updated_at, created_at from wisdom.notes order by updatedAt DESC LIMIT ? OFFSET ?";
  private static final String GET_LATEST = "select * from wisdom.note order by updated_at DESC LIMIT 1";
  private static final String FIND_BY_TEXT = "select * from wisdom.note where content ILIKE ? order by updated_at DESC";

  public List<Note> findAll(int pageNumber, int limit){
    List<Note> notes = new ArrayList<>();
    Connection connection = DatabaseUtils.getConnection();
    int offset = ((pageNumber -1) * limit);
    try(PreparedStatement statement = connection.prepareStatement(GET_ALL_PAGED)){
      statement.setInt(1, limit);
      statement.setInt(2, offset);
      ResultSet rs = statement.executeQuery();
      notes = this.processResultSet(rs);
      rs.close();
    }catch (SQLException e){
      DatabaseUtils.handleSqlException("NoteDao.findAll", e, LOGGER);
    }
    return notes;
  }

  @Override
  public Note create(NoteDraft entity) {
    Connection connection = DatabaseUtils.getConnection();
    Note note = null;

    try{
      connection.setAutoCommit(false);
      PreparedStatement statement = connection.prepareStatement(CREATE);
      statement.setString(1, entity.getTitle());
      statement.setString(2, entity.getContent());
      ResultSet rs = statement.executeQuery();
      List<Note> notes = this.processResultSet(rs);
      if (!notes.isEmpty()) {
        note = notes.get(0);
      }
      connection.commit();
      rs.close();
      statement.close();
    }catch (SQLException e) {
      try{
        connection.rollback();
      }catch(SQLException sqle){
        DatabaseUtils.handleSqlException("NoteDao.create.rollback", sqle, LOGGER);
      }
      DatabaseUtils.handleSqlException("NoteDao.create", e, LOGGER);
    }

    return note;
  }

  @Override
  public void delete(long id) {
    Connection connection = DatabaseUtils.getConnection();
    try {
      connection.setAutoCommit(false);
      PreparedStatement statement = connection.prepareStatement(DELETE);
      statement.setLong(1, id);
      statement.execute();
      connection.commit();
      statement.close();
    } catch (SQLException e) {
      try {
        connection.rollback();
      } catch (SQLException sqle) {
        DatabaseUtils.handleSqlException("NoteDao.delete.rollback", sqle, LOGGER);
      }
      DatabaseUtils.handleSqlException("NoteDao.delete", e, LOGGER);
    }
  }

  @Override
  public Optional<Note> findById(long id) {
    try (PreparedStatement statement = DatabaseUtils.getConnection().prepareStatement(GET_ONE)) {
      statement.setLong(1, id);
      ResultSet rs = statement.executeQuery();
      List<Note> notes = this.processResultSet(rs);
      rs.close();
      if (notes.isEmpty()) {
        return Optional.empty();
      }
      return Optional.of(notes.get(0));
    } catch (SQLException e) {
      DatabaseUtils.handleSqlException("NoteDao.findById", e, LOGGER);
    }
    return null;
  }

  public Optional<Note> findLatest() {
    List<Note> notes = findAll(1, 1);
    return notes.isEmpty() ? Optional.empty() : Optional.of(notes.get(0));
  }

  public List<Note> findByText(String text) {
    Connection connection = DatabaseUtils.getConnection();
    List<Note> notes = new ArrayList<>();

    try {
      PreparedStatement statement = connection.prepareStatement(FIND_BY_TEXT);
      statement.setString(1, "%" + text + "%");
      ResultSet rs = statement.executeQuery();
      notes = this.processResultSet(rs);
      rs.close();
      statement.close();
    } catch (SQLException e) {
      DatabaseUtils.handleSqlException("NoteDao.findByText", e, LOGGER);
    }

    return notes;
  }

  public void importBatch(List<NoteDraft> drafts) {
    if (drafts == null || drafts.isEmpty()) return;
    Connection connection = DatabaseUtils.getConnection();

    try {
      connection.setAutoCommit(false);
      PreparedStatement statement = connection.prepareStatement(CREATE);

      for (NoteDraft draft : drafts) {
        statement.setString(1, draft.getTitle());
        statement.setString(2, draft.getContent());
        statement.addBatch();
      }

      statement.executeBatch();
      connection.commit();
      statement.close();

    } catch (SQLException e) {
      try {
        connection.rollback();
      } catch (SQLException rollbackEx) {
        DatabaseUtils.handleSqlException("NoteDao.importBatch.rollback", rollbackEx, LOGGER);
      }
      DatabaseUtils.handleSqlException("NoteDao.importBatch", e, LOGGER);
    }
  }

  private List<Note> processResultSet(ResultSet rs) throws SQLException {
    List<Note> notes = new ArrayList<>();
    while (rs.next()) {
      Note note = new Note();
      note.setId(rs.getLong("id"));
      note.setTitle(rs.getString("title"));
      note.setContent(rs.getString("content"));
      note.setUpdatedAt(rs.getString("updated_at"));
      note.setCreatedAt(rs.getString("created_at"));
      notes.add(note);
    }
    return notes;
  }
  
}

