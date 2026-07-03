import { observer } from "mobx-react-use-autorun";
import { FormattedMessage } from "react-intl";
import { format } from "date-fns";
import type { OrganizeModel } from "@/model/OrganizeModel";

type Props = {
    organize: OrganizeModel;
}

export default observer((props: Props) => {

    return <>
        <div className="flex flex-row">
            <div className="flex flex-row" style={{ marginRight: "1em" }}>
                <FormattedMessage id="OrganizeName" defaultMessage="Organize Name" />
                {":"}
            </div>
            <div className="flex flex-row">
                {props.organize.name}
            </div>
        </div>
        <div className="flex flex-row">
            <div className="flex flex-row" style={{ marginRight: "1em" }}>
                <FormattedMessage id="ID" defaultMessage="ID" />
                {":"}
            </div>
            <div className="flex flex-row">
                {props.organize.id}
            </div>
        </div>
        <div className="flex flex-row">
            <div className="flex flex-row" style={{ marginRight: "1em" }}>
                <FormattedMessage id="OrganizeType" defaultMessage="Organize Type" />
                {":"}
            </div>
            <div className="flex flex-row">
                {props.organize.organizeType}
            </div>
        </div>
        <div className="flex flex-row">
            <div className="flex flex-row" style={{ marginRight: "1em" }}>
                <FormattedMessage id="CreateDate" defaultMessage="Create Date" />
                {":"}
            </div>
            <div className="flex flex-row">
                {format(props.organize.createDate, "yyyy-MM-dd HH:mm:ss")}
            </div>
        </div>
        <div className="flex flex-row">
            <div className="flex flex-row" style={{ marginRight: "1em" }}>
                <FormattedMessage id="UpdateDate" defaultMessage="Update Date" />
                {":"}
            </div>
            <div className="flex flex-row">
                {format(props.organize.updateDate, "yyyy-MM-dd HH:mm:ss")}
            </div>
        </div>
    </>
})