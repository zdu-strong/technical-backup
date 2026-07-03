import { jsonArrayMember, jsonMember, jsonObject } from 'typedjson'
import { makeAutoObservable } from 'mobx-react-use-autorun'

@jsonObject
export class SuperAdminRoleQueryPaginationModel {

    @jsonMember(Number)
    pageNum: number = 1;

    @jsonMember(Number)
    pageSize: number = 1;

    @jsonMember(String)
    organizeId: string = "";

    @jsonArrayMember(String)
    permissionList: string[] = [];

    @jsonMember(String)
    roleName: string = "";

    @jsonMember(Boolean)
    isOnlySystemRole: boolean = false;

    constructor() {
        makeAutoObservable(this);
    }

}
