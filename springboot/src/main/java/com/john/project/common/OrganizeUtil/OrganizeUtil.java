package com.john.project.common.OrganizeUtil;

import java.util.ArrayDeque;
import java.util.Date;

import com.john.project.model.OrganizeModel;
import com.john.project.service.PermissionRelationService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.john.project.common.LongTermTaskUtil.LongTermTaskUtil;
import com.john.project.enums.LongTermTaskTypeEnum;
import com.john.project.model.LongTermTaskUniqueKeyModel;
import com.john.project.service.OrganizeRelationService;
import com.john.project.service.OrganizeService;

import lombok.SneakyThrows;

@Component
public class OrganizeUtil {

    @Autowired
    private OrganizeService organizeService;

    @Autowired
    private OrganizeRelationService organizeRelationService;

    @Autowired
    private PermissionRelationService permissionRelationService;

    @Autowired
    private LongTermTaskUtil longTermTaskUtil;

    @Autowired
    private ObjectMapper objectMapper;

    public void move(String id, String parentId) {
        this.organizeService.checkCanBeMoveOfOrganize(id, parentId);
        var expectException = new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Too many requests to move the organize, please wait a minute and try again");
        var uniqueKeyList = this.getUniqueKeyList(id, parentId);
        this.longTermTaskUtil.runRetryWhenExists(() -> {
            this.checkUniqueKeyList(id, parentId, uniqueKeyList);
            this.organizeService.checkCanBeMoveOfOrganize(id, parentId);
            this.organizeService.move(id, parentId);
        }, expectException, uniqueKeyList);

        this.refresh(id);
    }

    public OrganizeModel create(OrganizeModel organizeModel) {
        var organize = this.organizeService.create(organizeModel);
        this.refresh(organize.getId());
        return organize;
    }

    public void update(OrganizeModel organizeModel) {
        this.organizeService.update(organizeModel);
    }

    public void delete(String id) {
        this.organizeService.delete(id);
        this.refresh(id);
    }

    private void refresh(String organizeId) {
        var deadline = this.getDeadline();

        while (!new Date().after(deadline)) {
            var hasNext = this.permissionRelationService.refresh(organizeId);
            if (hasNext) {
                continue;
            }
            break;
        }

        var arrayDeque = new ArrayDeque<String>();
        arrayDeque.add(organizeId);
        while (!arrayDeque.isEmpty() && !new Date().after(deadline)) {
            organizeId = arrayDeque.pop();

            var hasNext = this.organizeRelationService.refresh(organizeId);
            if (hasNext) {
                arrayDeque.addFirst(organizeId);
                continue;
            }

            var childIdList = this.organizeService.getChildIdList(organizeId);
            for (var childId : childIdList) {
                arrayDeque.add(childId);
            }
        }
    }

    private Date getDeadline() {
        return DateUtils.addSeconds(new Date(), 10);
    }

    @SneakyThrows
    private void checkUniqueKeyList(String id, String parentId, LongTermTaskUniqueKeyModel[] uniqueKeyList) {
        var uniqueKeyListTwo = this.getUniqueKeyList(id, parentId);
        if (!this.objectMapper.writeValueAsString(uniqueKeyList)
                .equals(this.objectMapper.writeValueAsString(uniqueKeyListTwo))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Too many requests to move the organize, please wait a minute and try again");
        }
    }

    private LongTermTaskUniqueKeyModel[] getUniqueKeyList(String id, String parentId) {
        var uniqueKeyList = Lists.newArrayList(id, parentId)
                .stream()
                .filter(s -> StringUtils.isNotBlank(s))
                .map(s -> this.organizeService.getTopOrganize(s))
                .map(s -> {
                    var longTermTaskUniqueKey = new LongTermTaskUniqueKeyModel();
                    longTermTaskUniqueKey.setType(LongTermTaskTypeEnum.MOVE_ORGANIZE.getValue());
                    longTermTaskUniqueKey.setUniqueKey(s.getId());
                    return longTermTaskUniqueKey;
                })
                .toList()
                .toArray(new LongTermTaskUniqueKeyModel[]{});
        return uniqueKeyList;
    }

}
