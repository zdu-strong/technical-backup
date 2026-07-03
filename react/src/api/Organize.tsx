import { OrganizeModel } from "@model/OrganizeModel";
import axios from "axios";
import { TypedJSON } from "typedjson";

export async function getOrganizeById(id: string) {
    const { data } = await axios.get("/organize", { params: { id: id } });
    return new TypedJSON(OrganizeModel).parse(data)!;
}

export async function createOrganize(organize: OrganizeModel) {
    const { data } = await axios.post("/organize/create", organize);
    return new TypedJSON(OrganizeModel).parse(data)!;
}

export async function updateOrganize(organize: OrganizeModel) {
    await axios.post("/organize/update", organize);
}

export async function deleteOrganizeById(id: string) {
    await axios.post("/organize/delete", null, { params: { id: id } });
}