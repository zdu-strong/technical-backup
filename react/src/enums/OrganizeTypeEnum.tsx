import { makeAutoObservable } from "mobx-react-use-autorun";
import { jsonMember, jsonObject } from "typedjson";
import linq from "linq";

@jsonObject
export class OrganizeTypeEnum {

    static COUNTRY = new OrganizeTypeEnum("COUNTRY");

    static ALLIANCE = new OrganizeTypeEnum("ALLIANCE");

    static GOVERNANCE_REGION = new OrganizeTypeEnum("GOVERNANCE_REGION");

    static ORGANIZE = new OrganizeTypeEnum("ORGANIZE");

    @jsonMember(String)
    value!: string;

    static parse(value: string) {
        return linq.from(OrganizeTypeEnum.values()).where(s => s.value === value).single();
    }

    static values() {
        return Object.getOwnPropertyNames(OrganizeTypeEnum)
            .map(s => (OrganizeTypeEnum as any)[s])
            .filter(s => s instanceof OrganizeTypeEnum);
    }

    constructor(value: string) {
        makeAutoObservable(this);
        this.value = value;
    }
}
