import { PaginationModel } from "@model/PaginationModel";
import { SuperAdminRoleQueryPaginationModel } from "@model/SuperAdminRoleQueryPaginationModel";
import { SystemRoleModel } from "@model/SystemRoleModel";
import axios from "axios";

export async function searchByPagination(query: SuperAdminRoleQueryPaginationModel) {
  const { data } = await axios.get("/super-admin/role/search/pagination", { params: query });
  return PaginationModel.fromJson(data, SystemRoleModel);
}
