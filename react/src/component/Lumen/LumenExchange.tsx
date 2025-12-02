import { faArrowDownShortWide, faDollarSign, faMoneyBill1Wave, faMoneyCheckDollar, faSpinner } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Button, IconButton, TextField } from "@mui/material";
import { observer, useMobxState } from "mobx-react-use-autorun";
import api from '@api';
import { MessageService } from "@/common/MessageService";
import { useMultipleSubmit, useQuery } from "@/common/use-hook";
import { Big, RoundingMode } from 'bigdecimal.js';

export default observer(() => {

    const state = useMobxState({
        isUsd: true,
        sourceCurrencyBalance: "0",
        targetCurrencyBalance: "0",
    });

    const exchangePreviewState = useQuery(async () => {
        const exchangeResult = await api.Lumen.exchangePreview(state.isUsd ? "USD" : "JAPAN", state.sourceCurrencyBalance);
        state.targetCurrencyBalance = exchangeResult.setScale(2, RoundingMode.FLOOR).toPlainString();
    });

    const exchangeState = useMultipleSubmit(async () => {
        const exchangeResult = await api.Lumen.exchange(state.isUsd ? "USD" : "JAPAN", state.sourceCurrencyBalance);
        const targetCurrencyBalance = exchangeResult.setScale(2, RoundingMode.FLOOR).toPlainString();
        exchangePreviewState.requery();
        MessageService.success(`exhange success! you get ${targetCurrencyBalance} ${state.isUsd ? "JAPAN" : "USD"}`);
        state.sourceCurrencyBalance = "0";
        exchangePreviewState.requery();
    });

    function exchangePreview(e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) {
        state.sourceCurrencyBalance = getNumberOrEmptyString(state.sourceCurrencyBalance, e.target.value);
        exchangePreviewState.requery();
    }

    function getNumberOrEmptyString(originValue: string, value: string) {
        value = value.trim();
        if(value === ""){
            return value;
        }
        try {
            new Big(value);

            {
                const regExp = new RegExp("^0+(?=[1-9])", "g");
                if (regExp.test(value)) {
                    return value.replaceAll(regExp, "");
                }
            }

            {
                const regExp = new RegExp("(?<=^0)0+(?=[\\.])", "g");
                if (regExp.test(value)) {
                    return value.replaceAll(regExp, "");
                }
            }

            {
                const regExp = new RegExp("^0+$", "g");
                if (regExp.test(value)) {
                    return "0";
                }
            }

            return value;
        } catch (e) {
            // do nothing
        }
        return originValue;
    }

    function transform() {
        state.isUsd = !state.isUsd;
        exchangePreviewState.requery();
    }

    return <div className="flex flex-col flex-auto justify-center items-center">
        <div style={{ paddingBottom: "1em" }}>
            <TextField
                label={state.isUsd ? "USD" : "JAPAN"}
                type="text"
                autoComplete="off"
                value={state.sourceCurrencyBalance}
                onChange={exchangePreview}
                slotProps={{
                    input: {
                        startAdornment: <IconButton
                            color="primary"
                        >
                            <FontAwesomeIcon icon={state.isUsd ? faDollarSign : faMoneyBill1Wave} />
                        </IconButton>
                    }
                }}
            />
        </div>
        <div style={{ paddingBottom: "1em" }}>
            <IconButton
                color="primary"
                onClick={transform}
            >
                <FontAwesomeIcon icon={faArrowDownShortWide} />
            </IconButton>
        </div>
        <div style={{ paddingBottom: "1em" }}>
            <TextField
                label={state.isUsd ? "JAPAN" : "USD"}
                type="text"
                autoComplete="off"
                disabled
                value={state.targetCurrencyBalance}
                slotProps={{
                    input: {
                        startAdornment: <IconButton
                            color="primary"
                        >
                            <FontAwesomeIcon icon={state.isUsd ? faMoneyBill1Wave : faDollarSign} />
                        </IconButton>
                    }
                }}
            />
        </div>
        <div className="flex flex-row">
            <Button
                variant="contained"
                startIcon={<FontAwesomeIcon icon={exchangeState.loading ? faSpinner : faMoneyCheckDollar} spin={exchangeState.loading} />}
                onClick={exchangeState.resubmit}
            >
                {"exchange"}
            </Button>
        </div>
    </div>
})