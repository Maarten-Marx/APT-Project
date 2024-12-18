package me.maartenmarx.threadservice.controller;

import me.maartenmarx.common.dto.ThreadRequest;
import me.maartenmarx.common.dto.ThreadResponse;
import me.maartenmarx.common.dto.ThreadsResponse;
import me.maartenmarx.common.service.JwtService;
import lombok.RequiredArgsConstructor;
import me.maartenmarx.threadservice.service.ThreadService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/threads")
public class ThreadController {
    private final ThreadService threadService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ThreadsResponse getAll() {
        return threadService.getAllThreads();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ThreadResponse getThread(@PathVariable Long id) {
        return threadService.getById(id);
    }

    @GetMapping("/user/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ThreadsResponse getByUser(@PathVariable String id) {
        return threadService.getByUser(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void createThread(@RequestBody ThreadRequest request, @RequestHeader("Authorization") String bearer) {
        var userData = JwtService.getUserData(bearer);

        threadService.createThread(request, userData);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateThread(@PathVariable Long id, @RequestBody ThreadRequest request, @RequestHeader("Authorization") String bearer) {
        var userData = JwtService.getUserData(bearer);

        threadService.updateThread(id, request, userData);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteThread(@PathVariable Long id, @RequestHeader("Authorization") String bearer) {
        var userData = JwtService.getUserData(bearer);

        threadService.deleteThread(id, userData);
    }
}
