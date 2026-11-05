import { PaginationModel } from "@model/PaginationModel";
import { OrganizeModel } from "@model/OrganizeModel";
import axios from "axios";
import { SuperAdminOrganizeQueryPaginationModel } from "@model/SuperAdminOrganizeQueryPaginationModel";

export async function searchByPagination(query: SuperAdminOrganizeQueryPaginationModel) {
    const { data } = await axios.post("/manage/organize/search/pagination", query);
    return PaginationModel.fromJson(data, OrganizeModel);
}
