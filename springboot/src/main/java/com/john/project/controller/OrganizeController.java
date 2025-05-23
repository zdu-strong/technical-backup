package com.john.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.john.project.common.baseController.BaseController;
import com.john.project.model.OrganizeModel;

@RestController
public class OrganizeController extends BaseController {

    @PostMapping("/organize/create")
    public ResponseEntity<?> create(@RequestBody OrganizeModel organizeModel) {
        this.permissionUtil.checkIsSignIn(request);
        this.organizeService.checkHasExistOfParentOrganize(organizeModel);

        var organize = this.organizeUtil.create(organizeModel);
        return ResponseEntity.ok(organize);
    }

    @PutMapping("/organize/update")
    public ResponseEntity<?> update(@RequestBody OrganizeModel organizeModel) {
        this.permissionUtil.checkIsSignIn(request);
        this.validationFieldUtil.checkNotBlankOfId(organizeModel.getId());
        this.organizeService.checkHasExistById(organizeModel.getId());

        this.organizeUtil.update(organizeModel);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/organize/delete")
    public ResponseEntity<?> delete(@RequestParam String id) {
        this.permissionUtil.checkIsSignIn(request);
        this.validationFieldUtil.checkNotBlankOfId(id);
        this.organizeService.checkHasExistById(id);

        this.organizeUtil.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/organize/move")
    public ResponseEntity<?> move(@RequestParam String id, @RequestParam(required = false) String parentId) {
        this.permissionUtil.checkIsSignIn(request);
        this.validationFieldUtil.checkNotBlankOfId(id);
        this.organizeService.checkHasExistById(id);
        this.organizeService.checkHasExistById(parentId);

        this.organizeUtil.move(id, parentId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/organize")
    public ResponseEntity<?> getOrganizeById(@RequestParam String id) {
        this.permissionUtil.checkIsSignIn(request);
        this.validationFieldUtil.checkNotBlankOfId(id);
        this.organizeService.checkHasExistById(id);

        var organizeModel = this.organizeService.getById(id);
        return ResponseEntity.ok(organizeModel);
    }

}
