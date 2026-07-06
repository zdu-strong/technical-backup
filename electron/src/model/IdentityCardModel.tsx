import { jsonMember, jsonObject, TypedJSON } from 'typedjson'
import { makeAutoObservable } from 'mobx-react-use-autorun'
import { UserModel } from '@model/UserModel';
import { OrganizeModel } from '@model/OrganizeModel';

@jsonObject
export class IdentityCardModel {

    @jsonMember(String)
    id!: string;

    @jsonMember(String)
    identityType!: string;

    @jsonMember(String)
    address!: string;

    @jsonMember(Date)
    createDate!: Date;

    @jsonMember(Date)
    updateDate!: Date;

    @jsonMember({ deserializer: (value: any) => new TypedJSON(UserModel).parse(value) })
    user!: UserModel;

    @jsonMember({ deserializer: (value: any) => new TypedJSON(OrganizeModel).parse(value) })
    topOrganize!: OrganizeModel;

    @jsonMember({ deserializer: (value: any) => new TypedJSON(OrganizeModel).parse(value) })
    governanceRegion!: OrganizeModel;

    constructor() {
        makeAutoObservable(this);
    }

}
