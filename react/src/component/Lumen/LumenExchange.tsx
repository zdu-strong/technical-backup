import { Button, TextField } from "@mui/material";
import { observer, useMobxState } from "mobx-react-use-autorun";

export default observer(() => {

  const state = useMobxState({


  });
  

  return <div className="flex flex-col flex-auto justify-center items-center">
    <div style={{ paddingBottom: "1em" }}>
      <TextField
        type="number"
        autoComplete="off"
        defaultValue={0}
      />
    </div>
    <div style={{ paddingBottom: "1em" }}>
      <TextField
        type="number"
        autoComplete="off"
        disabled
        defaultValue={0}
      />
    </div>
    <div className="flex flex-row">
      <Button
        variant="contained"
      >
        {"exchange"}
      </Button>
    </div>
  </div>
})