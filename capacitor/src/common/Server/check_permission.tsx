import { SystemPermissionEnum } from "@/enums/SystemPermissionEnum";
import { GlobalUserInfo } from "@common/Server/get_global_user_info";
import linq from "linq";

export function checkAnyPermission(permission: SystemPermissionEnum | SystemPermissionEnum[]) {
    if (!hasAnyPermission(permission)) {
        throw new Error("permission denied");
    }
}

export function hasAnyPermission(permission: SystemPermissionEnum | SystemPermissionEnum[]): boolean {
    if (permission instanceof SystemPermissionEnum) {
        const permissionEnum = permission as SystemPermissionEnum;
        if (linq.from(GlobalUserInfo.roleList)
            .selectMany(s => s.permissionList)
            .where(s => s.permission === permissionEnum.value)
            .isEmpty()
        ) {
            return false;
        }
    } else {
        const permissionList = permission as SystemPermissionEnum[];
        if (linq.from(permissionList)
            .where(s => hasAnyPermission(s))
            .isEmpty()
        ) {
            return false;
        }
    }
    return true;
}