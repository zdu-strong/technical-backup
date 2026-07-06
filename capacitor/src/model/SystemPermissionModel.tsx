import { makeAutoObservable } from "mobx-react-use-autorun";
import { jsonMember, jsonObject } from "typedjson";
import { OrganizeModel } from "@model/OrganizeModel";

@jsonObject
export class SystemPermissionModel {

    @jsonMember(String)
    id!: string;

    @jsonMember(String)
    permission!: string;

    @jsonMember(Date)
    createDate!: Date;

    @jsonMember(Date)
    updateDate!: Date;

    @jsonMember(OrganizeModel)
    organize!: OrganizeModel;

    constructor() {
        makeAutoObservable(this);
    }
}
