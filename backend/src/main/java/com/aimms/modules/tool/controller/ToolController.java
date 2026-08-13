package com.aimms.modules.tool.controller;

import com.aimms.common.R;
import com.aimms.modules.tool.entity.AiModel;
import com.aimms.modules.tool.entity.AiTool;
import com.aimms.modules.tool.service.AiModelService;
import com.aimms.modules.tool.service.AiToolService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tool")
@RequiredArgsConstructor
public class ToolController {

    private final AiToolService aiToolService;
    private final AiModelService aiModelService;

    @GetMapping
    public R<List<AiTool>> listTools() {
        return R.ok(aiToolService.list());
    }

    @GetMapping("/{id}")
    public R<AiTool> getTool(@PathVariable Integer id) {
        return R.ok(aiToolService.getById(id));
    }

    @PostMapping
    public R<Void> saveTool(@RequestBody AiTool tool) {
        aiToolService.save(tool);
        return R.ok();
    }

    @PutMapping("/{id}")
    public R<Void> updateTool(@PathVariable Integer id, @RequestBody AiTool tool) {
        tool.setId(id);
        aiToolService.updateById(tool);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> deleteTool(@PathVariable Integer id) {
        aiToolService.removeById(id);
        return R.ok();
    }

    @GetMapping("/{toolId}/model")
    public R<List<AiModel>> listModels(@PathVariable Integer toolId) {
        return R.ok(aiModelService.listByToolId(toolId));
    }

    @PostMapping("/{toolId}/model")
    public R<Void> saveModel(@PathVariable Integer toolId, @RequestBody AiModel model) {
        model.setToolId(toolId);
        aiModelService.save(model);
        return R.ok();
    }

    @PutMapping("/model/{id}")
    public R<Void> updateModel(@PathVariable Integer id, @RequestBody AiModel model) {
        model.setId(id);
        aiModelService.updateById(model);
        return R.ok();
    }

    @DeleteMapping("/model/{id}")
    public R<Void> deleteModel(@PathVariable Integer id) {
        aiModelService.removeById(id);
        return R.ok();
    }
}
