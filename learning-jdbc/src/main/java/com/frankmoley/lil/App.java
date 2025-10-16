package com.frankmoley.lil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.frankmoley.lil.data.dao.NoteDao;
import com.frankmoley.lil.data.entity.Note;
import com.frankmoley.lil.data.entity.NoteDraft;
import com.frankmoley.lil.data.util.DataAccessException;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        NoteDao noteDao = new NoteDao();

        System.out.println("=== CREATE ===");
        NoteDraft d1 = new NoteDraft("Test 1", "Content 1");
        NoteDraft d2 = new NoteDraft("Test 2", "Content 2");
        NoteDraft d3 = new NoteDraft("Test 3", "Content 3");

        Note n1 = noteDao.create(new Note(d1.getTitle(), d1.getContent()));
        Note n2 = noteDao.create(new Note(d2.getTitle(), d2.getContent()));
        Note n3 = noteDao.create(new Note(d3.getTitle(), d3.getContent()));

        System.out.println(n1);
        System.out.println(n2);
        System.out.println(n3);

        System.out.println("\n=== FIND ALL (page 1, limit 5) ===");
        List<Note> notesPaged = noteDao.findAll(1, 5);
        notesPaged.forEach(System.out::println);

        System.out.println("\n=== FIND BY ID ===");
        Optional<Note> found = noteDao.findById(n1.getId());
        found.ifPresent(System.out::println);

        System.out.println("\n=== FIND ALL ===");
        List<Note> allNotes = noteDao.findAll();
        allNotes.forEach(System.out::println);

        System.out.println("\n=== DELETE ===");
        noteDao.delete(n2.getId());

        System.out.println("\n=== FIND ALL ===");
        List<Note> notesAfterDelete = noteDao.findAll();
        notesAfterDelete.forEach(System.out::println);

        System.out.println("\n=== IMPORT BATCH ===");
        List<NoteDraft> batch = new ArrayList<>();
        batch.add(new NoteDraft("Batch A", "Atomic insert test A"));
        batch.add(new NoteDraft("Batch B", "Atomic insert test B"));
        batch.add(new NoteDraft("Batch C", "Atomic insert test C"));
        batch.add(new NoteDraft("Batch D", "Atomic insert test D"));
        batch.add(new NoteDraft("Batch E", "Atomic insert test E"));

        try {
            noteDao.importBatch(batch);
            System.out.println("Batch import successful");
        } catch (DataAccessException e) {
            System.err.println("Batch import failed — rollback done");
            e.printStackTrace();
        }

        System.out.println("\n=== FIND ALL AFTER BATCH ===");
        noteDao.findAll(1, 10).forEach(System.out::println);
    }
}
