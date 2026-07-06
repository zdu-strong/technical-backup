import { Big, BigDecimal } from 'bigdecimal.js';
import { TypedJSON } from 'typedjson';

TypedJSON.mapType(BigDecimal, {
    deserializer: (value) => value === null || value === undefined || typeof value === "string" && value === "" ? null : Big(value),
})