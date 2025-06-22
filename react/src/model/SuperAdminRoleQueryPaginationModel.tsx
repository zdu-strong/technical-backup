import { jsonMember, jsonObject } from 'typedjson'
import { makeAutoObservable } from 'mobx-react-use-autorun'

@jsonObject
export class SuperAdminRoleQueryPaginationModel {

  @jsonMember(Number)
  pageNum: number = 1;

  @jsonMember(Number)
  pageSize: number = 1;

  constructor() {
    makeAutoObservable(this);
  }

}
