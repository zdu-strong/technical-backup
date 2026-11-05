import api from "@/api";
import LoadingOrErrorComponent from "@/common/MessageService/LoadingOrErrorComponent";
import { useMultipleQuery, useOnceSubmitWhileTrue } from "@/common/use-hook";
import { faFloppyDisk, faSpinner, faXmark } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Button, Dialog, DialogActions, DialogContent, DialogTitle, Divider, Fab, FormControl, InputLabel, MenuItem, Select, TextField } from "@mui/material";
import { observer, useMobxState } from "mobx-react-use-autorun";
import { FormattedMessage } from "react-intl";
import { timer } from "rxjs";
import { OrganizeModel } from "@/model/OrganizeModel";
import { v4 } from "uuid";
import { OrganizeTypeEnum } from "@/enums/OrganizeTypeEnum";
import linq from "linq";

type Props = {
    id: string;
    parentId: string;
    organizeType: string;
    searchByPagination: () => void;
    closeDialog: () => void;
}

export default observer((props: Props) => {

    const state = useMobxState(() => {
        let allOrganizeTypeEnumList = OrganizeTypeEnum.values();
        if (!props.id && !props.parentId) {
            allOrganizeTypeEnumList = allOrganizeTypeEnumList.filter(s => [OrganizeTypeEnum.COUNTRY, OrganizeTypeEnum.ALLIANCE].includes(OrganizeTypeEnum.parse(s.value)));
        }
        if (!props.id && props.parentId && !props.organizeType) {
            allOrganizeTypeEnumList = allOrganizeTypeEnumList.filter(s => ![OrganizeTypeEnum.COUNTRY, OrganizeTypeEnum.ALLIANCE, OrganizeTypeEnum.GOVERNANCE_REGION].includes(OrganizeTypeEnum.parse(s.value)));
        }

        const organize = new OrganizeModel();
        organize.name = "";
        organize.parent = new OrganizeModel();
        organize.parent.id = props.parentId;
        organize.organizeType = linq.from(allOrganizeTypeEnumList)
            .orderByDescending(s => s.value === OrganizeTypeEnum.COUNTRY.value)
            .orderByDescending(s => s.value === props.organizeType)
            .select(s => s.value)
            .firstOrDefault("");
        return {
            organize: organize,
            submit: false,
            organizeTypeLabelId: v4(),
            allOrganizeTypeEnumList: allOrganizeTypeEnumList,
        };
    });

    const errors = {
        name() {
            return state.submit &&
                !state.organize.name &&
                "Please fill in the name";
        },
        hasError() {
            return Object.keys(errors)
                .filter(s => s !== "hasError")
                .some(s => (errors as any)[s]());
        }
    };

    const { ready, error } = useMultipleQuery(async () => {
        if (props.id) {
            state.organize = await api.Organize.getOrganizeById(props.id);
        }
    });

    const { loading, resubmit } = useOnceSubmitWhileTrue(async () => {
        state.submit = true;
        if (errors.hasError()) {
            return false;
        }
        await timer(500).toPromise();
        if (props.id) {
            await api.Organize.updateOrganize(state.organize);
        } else {
            await api.Organize.createOrganize(state.organize);
        }

        props.searchByPagination();
        props.closeDialog();
        return true;
    })

    function closeDialog(event: {}, reason: "backdropClick" | "escapeKeyDown") {
        if (reason === "backdropClick") {
            return;
        }
        props.closeDialog();
    }

    return <>
        <Dialog
            open={true}
            onClose={closeDialog}
            disableRestoreFocus={true}
            fullWidth={true}
        >
            <DialogTitle className="justify-between items-center flex-row flex-auto flex">
                <div className="flex flex-row items-center" >
                    {props.id
                        ? <FormattedMessage id="Update" defaultMessage="Update" />
                        : <FormattedMessage id="Create" defaultMessage="Create" />}
                </div>
                <Fab color="default" id="closeButton" onClick={props.closeDialog}>
                    <FontAwesomeIcon icon={faXmark} size="xl" />
                </Fab>
            </DialogTitle>
            <Divider />
            <DialogContent style={{ padding: "1em" }}>
                <LoadingOrErrorComponent ready={ready} error={error} >
                    <div className="flex flex-row">
                        <TextField
                            label={<FormattedMessage id="OrganizeName" defaultMessage="Organize Name" />}
                            defaultValue={state.organize.name}
                            onChange={(e) => state.organize.name = e.target.value}
                            error={!!errors.name()}
                            helperText={errors.name()}
                            autoFocus={true}
                        />
                    </div>
                    {!props.id && !props.organizeType && <div className="flex flex-row" style={{ marginTop: "1em" }}>
                        <FormControl fullWidth>
                            <InputLabel id={state.organizeTypeLabelId}>
                                <FormattedMessage id="OrganizeType" defaultMessage="Organize Type" />
                            </InputLabel>
                            <Select
                                disabled={!!props.organizeType}
                                labelId={state.organizeTypeLabelId}
                                value={state.organize.organizeType}
                                label={<FormattedMessage id="OrganizeType" defaultMessage="Organize Type" />}
                                onChange={(e) => state.organize.organizeType = e.target.value}
                            >
                                {state.allOrganizeTypeEnumList.map(organizeTypeEnum => <MenuItem
                                    key={organizeTypeEnum.value}
                                    value={organizeTypeEnum.value}
                                >
                                    {organizeTypeEnum.value}
                                </MenuItem>)}
                            </Select>
                        </FormControl>
                    </div>}
                </LoadingOrErrorComponent>
            </DialogContent>
            {ready && <>
                <Divider />
                <DialogActions>
                    <Button
                        variant="contained"
                        onClick={resubmit}
                        startIcon={<FontAwesomeIcon icon={loading ? faSpinner : faFloppyDisk} spin={loading} />}
                    >
                        <FormattedMessage id="Save" defaultMessage="Save" />
                    </Button>
                </DialogActions>
            </>}
        </Dialog>
    </>
})