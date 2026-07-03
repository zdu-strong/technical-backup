import { jsonArrayMember, jsonMember, jsonObject } from 'typedjson'
import { UserEmailModel } from '@model/UserEmailModel';
import { makeAutoObservable } from 'mobx-react-use-autorun'
import { SystemRoleModel } from '@model/SystemRoleModel';
import { IdentityCardModel } from '@model/IdentityCardModel';

@jsonObject
export class UserModel {

    @jsonMember(String)
    id!: string;

    @jsonMember(String)
    username!: string;

    @jsonMember(String)
    password!: string;

    @jsonArrayMember(UserEmailModel)
    userEmailList!: UserEmailModel[];

    @jsonArrayMember(SystemRoleModel)
    roleList!: SystemRoleModel[];

    @jsonArrayMember(IdentityCardModel)
    identityCardList!: IdentityCardModel[];

    @jsonMember(String)
    accessToken!: string;

    @jsonMember(Boolean)
    menuOpen!: boolean;

    @jsonMember(Date)
    createDate!: Date;

    @jsonMember(Date)
    updateDate!: Date;

    constructor() {
        makeAutoObservable(this);
    }
}