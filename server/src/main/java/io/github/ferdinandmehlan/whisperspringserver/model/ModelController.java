package io.github.ferdinandmehlan.whisperspringserver.model;

import io.github.ferdinandmehlan.whisperspringserver.model.api.ModelListResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing the models the server can transcribe with.
 *
 * <p>The controller only answers requests; which model is loaded and what to call it is
 * {@link ModelService} territory.
 */
@RestController
@RequestMapping("/api/models")
public class ModelController {

    private final ModelService modelService;

    /**
     * Creates a new ModelController.
     *
     * @param modelService the service that knows which model the server runs with
     */
    public ModelController(ModelService modelService) {
        this.modelService = modelService;
    }

    /**
     * Lists the models available to the server.
     *
     * @return the loaded model, or an empty list when no model is loaded
     */
    @GetMapping
    public ModelListResponse models() {
        return modelService.list();
    }
}
