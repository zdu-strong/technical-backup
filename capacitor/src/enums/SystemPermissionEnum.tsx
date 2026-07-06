import { makeAutoObservable } from "mobx-react-use-autorun";
import { jsonMember, jsonObject } from "typedjson";
import linq from "linq";

@jsonObject
export class SystemPermissionEnum {

    static SUPER_ADMIN = new SystemPermissionEnum("SUPER_ADMIN", true, false);

    static ORGANIZE_MANAGE = new SystemPermissionEnum("ORGANIZE_MANAGE", false, true);

    static ORGANIZE_VIEW = new SystemPermissionEnum("ORGANIZE_VIEW", false, true);

    @jsonMember(String)
    value!: string;

    @jsonMember(Boolean)
    isSuperAdmin!: boolean;

    @jsonMember(Boolean)
    isOrganizePermission!: boolean;

    static parse(value: string) {
        return linq.from(SystemPermissionEnum.values()).where(s => s.value === value).single();
    }

    static values() {
        return Object.getOwnPropertyNames(SystemPermissionEnum)
            .map(s => (SystemPermissionEnum as any)[s])
            .filter(s => s instanceof SystemPermissionEnum);
    }

    constructor(value: string, isSuperAdmin: boolean, isOrganizePermission: boolean) {
        makeAutoObservable(this);
        this.value = value;
        this.isSuperAdmin = isSuperAdmin;
        this.isOrganizePermission = isOrganizePermission;
    }
}
