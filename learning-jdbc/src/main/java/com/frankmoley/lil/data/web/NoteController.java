package com.frankmoley.lil.data.web;

import com.frankmoley.lil.data.entity.Note;
import com.frankmoley.lil.data.entity.NoteDraft;
import com.frankmoley.lil.data.service.NoteService;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static spark.Spark.*;

public class NoteController {
    private static final Gson gson = new Gson();
    private final NoteService service;

    public NoteController(NoteService service) {
        this.service = service;
    }

    public void start() {
        path("/api/notes", () -> {

            // POST /api/notes
            post("", "application/json", (req, res) -> {
                try {
                    NoteDraft draft = gson.fromJson(req.body(), NoteDraft.class);
                    Note note = service.create(draft);
                    res.status(201);
                    res.header("Location", "/api/notes/" + note.getId());
                    res.type("application/json");
                    return gson.toJson(service.toApiModel(note));
                } catch (JsonSyntaxException e) {
                    res.status(400);
                    return "{\"error\": \"Invalid JSON\"}";
                } catch (IllegalArgumentException e) {
                    res.status(422);
                    return "{\"error\": \"Unprocessable Entity\"}";
                } catch (Exception e) {
                    e.printStackTrace();
                    res.status(500);
                    return "{\"error\": \"Internal server error\"}";
                }
            });

            // GET /api/notes/{id}
            get("/:id", "application/json", (req, res) -> {
                long id;
                try {
                    id = Long.parseLong(req.params(":id"));
                    Optional<Note> note = service.findById(id);
                    if (note.isEmpty()) {
                    res.status(404);
                    return "{\"error\": \"Note not found\"}";
                }
                res.status(200);
                res.type("application/json");
                return gson.toJson(service.toApiModel(note.get()));
                } catch (NumberFormatException e) {
                    res.status(400);
                    return "{\"error\": \"Invalid ID\"}";
                } catch (Exception e) {
                    e.printStackTrace();
                    res.status(500);
                    return "{\"error\": \"Internal server error\"}";
                }
            });

            // GET /api/notes?page=&size=
            get("", "application/json", (req, res) -> {
                int page = 0;
                int size = 20;
                try {
                    if (req.queryParams("page") != null)
                        page = Integer.parseInt(req.queryParams("page"));
                    if (req.queryParams("size") != null)
                        size = Integer.parseInt(req.queryParams("size"));
                } catch (NumberFormatException e) {
                    res.status(400);
                    return "{\"error\": \" Invalid page or size\"}";
                }

                try {
                    List<Note> notes = service.findAll(page, size);
                    long totalCount = service.countAll();
                    res.status(200);
                    res.type("application/json");
                    res.header("X-Total-Count", String.valueOf(totalCount));
                    List<Map<String, Object>> jsonList = notes.stream()
                            .map(service::toApiModel)
                            .toList();
                    return gson.toJson(jsonList);
                } catch (Exception e) {
                    e.printStackTrace();
                    res.status(500);
                    return "{\"error\": \"Internal server error\"}";
                }
            });

            // DELETE /api/notes/{id}
            delete("/:id", (req, res) -> {
                try {
                    long id = Long.parseLong(req.params(":id"));
                    Optional<Note> existing = service.findById(id);
                    if (existing.isEmpty()) {
                        res.status(404);
                        return "{\"error\": \"Note not found\"}";
                    }
                    service.delete(id);
                    res.status(204);
                    return "";
                } catch (NumberFormatException e) {
                    res.status(400);
                    return "{\"error\": \"Invalid ID\"}";
                } catch (Exception e) {
                    e.printStackTrace();
                    res.status(500);
                    return "{\"error\": \"Internal server error\"}";
                }
            });
        });
    }
}

