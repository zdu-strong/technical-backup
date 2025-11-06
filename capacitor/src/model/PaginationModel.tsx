import { makeAutoObservable } from 'mobx-react-use-autorun';
import linq from "linq";
import * as mathjs from 'mathjs';
import { jsonArrayMember, jsonMember, jsonObject, type Serializable, TypedJSON } from 'typedjson';

export class PaginationModel<T> {

    pageNum: number = 1;

    pageSize: number = 1;

    totalRecords: number = 0;

    totalPages: number = 0;

    items: T[] = [];

    constructor() {
        makeAutoObservable(this);
    }

    static fromStream<T>(
        pageNum: number,
        pageSize: number,
        stream: linq.IEnumerable<T>
    ): PaginationModel<T> {

        if (pageNum < 1) {
            throw new Error("The page number cannot be less than 1");
        }
        if (pageSize < 1) {
            throw new Error("The page size cannot be less than 1");
        }

        if (pageNum !== Math.floor(pageNum!)) {
            throw new Error("The page number must be an integer");
        }

        if (pageSize !== Math.floor(pageSize!)) {
            throw new Error("The page size must be an integer");
        }

        const totalRecords = stream.count();
        const totalPages = Math.floor(mathjs.divide(totalRecords, pageSize)) + (mathjs.mod(totalRecords, pageSize) > 0 ? 1 : 0);
        const items = stream
            .skip(mathjs.multiply(pageNum - 1, pageSize))
            .take(pageSize)
            .toArray();

        const model = new PaginationModel<T>();
        model.pageNum = pageNum;
        model.pageSize = pageSize;
        model.totalPages = totalPages;
        model.totalRecords = totalRecords;
        model.items = items;
        return model;
    }

    static fromJson<U>(json: string | object, rootConstructor: Serializable<U>): PaginationModel<U> {
        @jsonObject
        class CustomPaginationModel {
            @jsonMember(Number)
            pageNum: number = 1;

            @jsonMember(Number)
            pageSize: number = 1;

            @jsonMember(Number)
            totalRecords: number = 0;

            @jsonMember(Number)
            totalPages: number = 0;

            @jsonArrayMember(rootConstructor)
            items: U[] = [];
        }

        const customPaginationModel = new TypedJSON(CustomPaginationModel).parse(json)!;

        const model = new PaginationModel<U>();
        model.pageNum = customPaginationModel.pageNum;
        model.pageSize = customPaginationModel.pageSize;
        model.totalPages = customPaginationModel.totalPages;
        model.totalRecords = customPaginationModel.totalRecords;
        model.items = customPaginationModel.items;
        return model;
    }

}