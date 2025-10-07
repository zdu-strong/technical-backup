import { faArrowDownShortWide, faDollarSign, faMoneyBill1Wave, faMoneyCheckDollar, faSpinner } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Button, IconButton, TextField } from "@mui/material";
import { observer, useMobxState } from "mobx-react-use-autorun";
import api from '@api';
import * as math from 'mathjs'
import { MessageService } from "@/common/MessageService";
import { useMultipleSubmit, useQuery } from "@/common/use-hook";

export default observer(() => {

  const state = useMobxState({
    isUsd: true,
    sourceCurrencyBalance: 0,
    targetCurrencyBalance: 0,
  });

  const exchangePreviewState = useQuery(async () => {
    const exchangeResult = await api.Lumen.exchangePreview(state.isUsd ? "USD" : "JAPAN", state.sourceCurrencyBalance);
    state.targetCurrencyBalance = math.floor(exchangeResult, 2);
  });

  const exchangeState = useMultipleSubmit(async () => {
    const exchangeResult = await api.Lumen.exchange(state.isUsd ? "USD" : "JAPAN", state.sourceCurrencyBalance);
    const targetCurrencyBalance = math.floor(exchangeResult, 2);
    exchangePreviewState.requery();
    MessageService.success(`exhange success! you get ${targetCurrencyBalance} ${state.isUsd ? "JAPAN" : "USD"}`);
  });

  function exchangePreview(e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) {
    const sourceValue = Number(e.target.value);
    if (sourceValue < Number.MAX_SAFE_INTEGER) {
      state.sourceCurrencyBalance = sourceValue;
      exchangePreviewState.requery();
    }
  }

  function transform() {
    state.isUsd = !state.isUsd;
    exchangePreviewState.requery();
  }

  return <div className="flex flex-col flex-auto justify-center items-center">
    <div style={{ paddingBottom: "1em" }}>
      <TextField
        label={state.isUsd ? "USD" : "JAPAN"}
        type="number"
        autoComplete="off"
        value={`${state.sourceCurrencyBalance}`}
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
        type="number"
        autoComplete="off"
        disabled
        value={`${state.targetCurrencyBalance}`}
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