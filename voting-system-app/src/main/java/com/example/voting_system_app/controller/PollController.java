package com.example.voting_system_app.controller;

import com.example.voting_system_app.entity.Option;
import com.example.voting_system_app.entity.Poll;
import com.example.voting_system_app.entity.PollData;
import com.example.voting_system_app.service.OptionService;
import com.example.voting_system_app.service.PollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class PollController {

    @Autowired
    private PollService pollService;

    @Autowired
    private OptionService optionService;

    /**
     * Home page displaying all polls.
     */
    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("polls", pollService.getAllPoll());
        return "index"; // Returns the index view to display the list of polls
    }

    /**
     * View a specific poll by ID.
     */
    @GetMapping("/poll/{id}")
    public String viewPoll(@PathVariable int id, Model model) {
        Poll poll = pollService.getPollById(id);
        if (poll == null) {
            // Handle invalid poll ID gracefully
            model.addAttribute("error", "Poll not found!");
            return "error";
        }
        model.addAttribute("poll", poll);
        return "poll"; // Returns the poll view
    }

    /**
     * Create a new poll along with its options.
     */
    @PostMapping("/poll")
    public String createPoll(@ModelAttribute PollData pollData) {
        // Validate PollData inputs
        if (pollData.getQuestion() == null || pollData.getQuestion().isEmpty()) {
            throw new IllegalArgumentException("Poll question cannot be empty");
        }
        if (pollData.getOptions() == null || pollData.getOptions().isEmpty()) {
            throw new IllegalArgumentException("Poll must have at least one option");
        }

        // Create Poll entity
        Poll poll = new Poll();
        poll.setQuestion(pollData.getQuestion());


        List<Option> optionList = new ArrayList<>();
        for (String optionStr : pollData.getOptions()) {
            if (optionStr == null || optionStr.trim().isEmpty()) {
                continue; // Skip empty options
            }
            Option option = new Option();
            option.setDesc(optionStr.trim());
            option.setPoll(poll);
            optionList.add(option);
        }
        poll.setOptionList(optionList);

        pollService.createPoll(poll);
        return "redirect:/";
    }

    /**
     * Create a vote for a specific option.
     */
    @PostMapping("/vote")
    public String createVote(int optionId) {
        // Validate optionId
        if (optionId <= 0) {
            throw new IllegalArgumentException("Invalid option ID");
        }

        optionService.createVote(optionId);
        return "redirect:/";
    }

    /**
     * Display poll results for a specific poll.
     */

    @GetMapping("/poll/{id}/results")
    public String pollResults(@PathVariable int id, Model model) {
        Poll poll = pollService.getPollById(id);
        if (poll == null) {
            model.addAttribute("error", "Poll not found!");
            return "error";
        }


        List<Option> options = poll.getOptionList();


        int maxVotes = options.stream()
                .mapToInt(Option::getVotes)
                .max()
                .orElse(0);


        List<Option> maxVotedOptions = new ArrayList<>();
        for (Option option : options) {
            if (option.getVotes() == maxVotes) {
                maxVotedOptions.add(option);
            }
        }

        // Determine the result message
        String resultMessage;
        if (maxVotedOptions.size() == 1) {

            resultMessage = "Congratulations to " + maxVotedOptions.get(0).getDesc() + "!";
        } else {

            resultMessage = "It's a tie! Please consider a revote.";
        }


        model.addAttribute("poll", poll);
        model.addAttribute("resultMessage", resultMessage);

        return "results";
    }



















}
