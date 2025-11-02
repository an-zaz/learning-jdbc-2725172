package com.frankmoley.lil.data.service;

import com.frankmoley.lil.data.dao.NoteDao;
import com.frankmoley.lil.data.entity.Note;
import com.frankmoley.lil.data.entity.NoteDraft;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class NoteService {
    private final NoteDao noteDao;

    public NoteService(NoteDao noteDao) {
        this.noteDao = noteDao;
    }

    public Note create(NoteDraft draft) {
        validateDraft(draft);
        return noteDao.create(draft);
    }

    public Optional<Note> findById(Long id) {
        return noteDao.findById(id);
    }

    public List<Note> findAll(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page must be higher than 0");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Size must be between 1 and 100");
        }
        return noteDao.findAll(page + 1, size);
    }

    public void delete(Long id) {
        noteDao.delete(id);
    }

    public long countAll() {
        return noteDao.countAll();
    }

    private void validateDraft(NoteDraft draft) {
        if (draft.getTitle() == null || draft.getTitle().isBlank()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (draft.getTitle().length() > 120) {
            throw new IllegalArgumentException("Title must be less than 120 characters");
        }
        if (draft.getContent() != null && draft.getContent().length() > 10_000) {
            throw new IllegalArgumentException("Content must be less than 10,000 characters");
        }
    }

    public Map<String, Object> toApiModel(Note note) {
        return Map.of(
                "id", note.getId(),
                "title", note.getTitle(),
                "content", note.getContent(),
                "createdAt", toIso8601(note.getCreatedAt()),
                "updatedAt", toIso8601(note.getUpdatedAt())
        );
    }

    public static String toIso8601(java.sql.Timestamp timestamp) {
        if (timestamp == null) return null;
        return DateTimeFormatter.ISO_INSTANT
                .format(timestamp.toInstant().atZone(ZoneOffset.UTC));
    }
}