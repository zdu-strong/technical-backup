import '@common/Server';
import registerWebworker from 'webworker-promise/lib/register'
import axios from "axios";
import { concatMap, from, lastValueFrom, map, of, range, timer, toArray } from "rxjs";
import { addMilliseconds } from 'date-fns'
import linq from 'linq'
import { getLongTermTask } from '@api/LongTermTask';
import { Big, RoundingMode } from "bigdecimal.js";

registerWebworker(async ({
    ServerAddress,
    file
}: {
    ServerAddress: string
    file: File
}, emit) => {
    if (!ServerAddress) {
        throw new Error("Server Address cannot be empty");
    }
    axios.defaults.baseURL = ServerAddress;
    for (let i = 10; i > 0; i--) {
        await timer(1).toPromise();
    }
    /* Each piece is 10MB */
    const everySize = 1024 * 1024 * 10;
    // unit is milliseconds
    const durationOfCalculateSpeed = 1000;
    const uplodStartDate = new Date();
    type UploadProgessType = {
        createDate: Date,
        loaded: number,
        total: number,
    };
    const uploadProgressList: UploadProgessType[] = [{
        createDate: new Date(),
        loaded: 0,
        total: file.size,
    }];
    const url = await lastValueFrom(range(1, Big(file.size).divide(everySize, 0, RoundingMode.CEILING).max(Big(1)).numberValue()).pipe(
        concatMap((pageNum) => {
            const formData = new FormData();
            formData.set("file", new File([file.slice((pageNum - 1) * everySize, pageNum * everySize)], file.name, file));
            return from(axios.post(`/upload/resource`, formData, {
                onUploadProgress(progressEvent) {
                    const loaded = Big((formData.get("file") as File).size).multiply(progressEvent.loaded).divide(progressEvent.total!, 0, RoundingMode.FLOOR).add((pageNum - 1) * everySize).numberValue();
                    const total = file.size;
                    const nowDate = new Date();
                    const beforeDate = addMilliseconds(nowDate, 0 - durationOfCalculateSpeed);
                    let uploadProgess: UploadProgessType = linq.from(uploadProgressList).where(s => s.createDate.getTime() <= beforeDate.getTime()).orderByDescending(s => s.createDate).firstOrDefault()!;
                    if (!uploadProgess) {
                        uploadProgess = linq.from(uploadProgressList).orderBy(s => s.createDate).first();
                    } else {
                        while (true) {
                            const index = uploadProgressList.findIndex(s => s.createDate.getTime() < uploadProgess.createDate.getTime());
                            if (index < 0) {
                                break;
                            }
                            uploadProgressList.splice(0, 1);
                        }
                    }
                    // unit is B/second
                    let speed = Big(loaded - uploadProgess.loaded).multiply(1000).divide(nowDate.getTime() - uploadProgess.createDate.getTime(), 0, RoundingMode.FLOOR).numberValue();
                    if (loaded === total) {
                        speed = Big(total).multiply(1000).divide(nowDate.getTime() - uplodStartDate.getTime(), 0, RoundingMode.FLOOR).numberValue();
                    }
                    uploadProgressList.push({
                        createDate: nowDate,
                        loaded,
                        total,
                    });
                    emit("onUploadProgress", {
                        total,
                        loaded,
                        speed,
                    });
                }
            }));
        }),
        map((response) => response.data),
        toArray(),
        concatMap((urlList) => {
            if (urlList.length === 1) {
                return of(linq.from(urlList).first());
            } else {
                return from(getLongTermTask(async () => axios.post<string>(`/upload/merge`, urlList)));
            }
        }),
        map((result) => result as string)
    ));
    return url;
});
