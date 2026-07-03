import { makeAutoObservable } from "mobx-react-use-autorun";
import { jsonArrayMember, jsonMember, jsonObject } from "typedjson";
import { SystemPermissionModel } from "@model/SystemPermissionModel";

@jsonObject
export class SystemRoleModel {

    @jsonMember(String)
    id!: string;

    @jsonMember(Date)
    createDate!: Date;

    @jsonMember(Date)
    updateDate!: Date;

    @jsonMember(String)
    name!: string;

    @jsonArrayMember(SystemPermissionModel)
    permissionList!: SystemPermissionModel[];

    constructor() {
        makeAutoObservable(this);
    }
}
