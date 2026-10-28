import { makeAutoObservable } from "mobx-react-use-autorun";
import { jsonMember, jsonObject } from "typedjson";
import linq from "linq";

@jsonObject
export class SystemPermissionEnum {

    static SUPER_ADMIN = new SystemPermissionEnum("SUPER_ADMIN", true, false);

    static ORGANIZE_OWNER = new SystemPermissionEnum("ORGANIZE_OWNER", false, true);

    static ORGANIZE_MEMBER = new SystemPermissionEnum("ORGANIZE_MEMBER", false, true);

    @jsonMember(String)
    value!: string;

    @jsonMember(Boolean)
    isSystemPermission!: boolean;

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

    constructor(value: string, isSystemPermission: boolean, isOrganizePermission: boolean) {
        makeAutoObservable(this);
        this.value = value;
        this.isSystemPermission = isSystemPermission;
        this.isOrganizePermission = isOrganizePermission;
    }
}
