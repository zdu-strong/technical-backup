import { faArrowDownShortWide, faDollarSign, faMoneyBill1Wave, faMoneyCheckDollar } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Button, IconButton, TextField } from "@mui/material";
import { observer, useMobxState, useMount } from "mobx-react-use-autorun";

export default observer(() => {

  const state = useMobxState({
    isUsd: true,
    sourceCurrencyBalance: 0,
    targetCurrencyBalance: 0
  });

  useMount((subscription) => {

  })

  return <div className="flex flex-col flex-auto justify-center items-center">
    <div style={{ paddingBottom: "1em" }}>
      <TextField
        type="number"
        autoComplete="off"
        defaultValue={0}
        slotProps={{
          input: {
            startAdornment: <IconButton
              color="primary"
            >
              <FontAwesomeIcon icon={faDollarSign} />
            </IconButton>
          }
        }}
      />
    </div>
    <div style={{ paddingBottom: "1em" }}>
      <IconButton
        color="primary"
      >
        <FontAwesomeIcon icon={faArrowDownShortWide} />
      </IconButton>
    </div>
    <div style={{ paddingBottom: "1em" }}>
      <TextField
        type="number"
        autoComplete="off"
        disabled
        defaultValue={0}
        slotProps={{
          input: {
            startAdornment: <IconButton
              color="primary"
            >
              <FontAwesomeIcon icon={faMoneyBill1Wave} />
            </IconButton>
          }
        }}
      />
    </div>
    <div className="flex flex-row">
      <Button
        variant="contained"
        startIcon={<FontAwesomeIcon icon={faMoneyCheckDollar} />}
      >
        {"exchange"}
      </Button>
    </div>
  </div>
})