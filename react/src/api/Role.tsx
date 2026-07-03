import { SystemRoleModel } from "@/model/SystemRoleModel";
import axios from "axios";
import { TypedJSON } from "typedjson";

export async function getRoleById(roleId: string) {
    const { data } = await axios.get("/role", { params: { id: roleId } });
    return new TypedJSON(SystemRoleModel).parse(data)!;
}

export async function deleteRoleById(roleId: string) {
    await axios.post("/role/delete", null, { params: { id: roleId } });
}

export async function createRole(role: SystemRoleModel) {
    const { data } = await axios.post("/role/create", role);
    return new TypedJSON(SystemRoleModel).parse(data)!;
}

export async function updateRole(role: SystemRoleModel) {
    await axios.post("/role/update", role);
}