import { PaginationModel } from "@model/PaginationModel";
import { SuperAdminUserQueryPaginationModel } from "@model/SuperAdminUserQueryPaginationModel";
import { UserModel } from "@model/UserModel";
import axios from "axios";

export async function searchByPagination(query: SuperAdminUserQueryPaginationModel) {
    const { data } = await axios.post("/manage/user/search/pagination", query);
    return PaginationModel.fromJson(data, UserModel);
}
