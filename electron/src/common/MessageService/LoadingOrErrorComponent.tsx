import { Alert, Skeleton } from "@mui/material"
import React from "react";
import { MESSAGE_TYPE_ENUM, getMessageObject } from "@common/MessageService";
import { observer } from 'mobx-react-use-autorun';

type Props = {
    ready: boolean;
    error: Error | Error[] | any;
    children?: React.ReactNode;
}

export default observer((props: Props) => {

    return <>
        {props.error && props.error instanceof Array && props.error.map(error => {
            const errorObject = getMessageObject(MESSAGE_TYPE_ENUM.error, error);
            return <Alert severity="error" style={{ margin: "1em", whiteSpace: "pre-wrap", wordBreak: "break-word", wordWrap: "break-word" }} key={errorObject.id}>
                {errorObject.message}
            </Alert>
        })}
        {props.error && !(props.error instanceof Array) && <Alert severity="error" style={{ margin: "1em", whiteSpace: "pre-wrap", wordBreak: "break-word", wordWrap: "break-word" }}>
            {getMessageObject(MESSAGE_TYPE_ENUM.error, props.error).message}
        </Alert>}
        {(!props.error || (props.error instanceof Array && props.error.length === 0)) && !props.ready && <div className="flex flex-auto flex-col" style={{ minHeight: "10em", minWidth: "100%", padding: "1em" }}>
            <Skeleton variant="text" />
            <Skeleton variant="circular" width={40} height={40} />
            <Skeleton variant="rectangular" className="flex flex-auto" />
        </div>}
        {(!props.error || (props.error instanceof Array && props.error.length === 0)) && props.ready && props.children}
    </>
})