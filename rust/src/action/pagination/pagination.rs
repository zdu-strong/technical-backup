use crate::model::pagination_model::PaginationModel;

pub async fn pagination_example() {
    let ref mut pagination_model = PaginationModel::new(1, 1, vec!["tom", "jerry"]);
    let ref mut pagination_json_string = serde_json::to_string(pagination_model).unwrap();
    println!("pagination_model json string: {:?}", pagination_json_string);
    let ref mut pagination_one_model: PaginationModel<String> =
        serde_json::from_str(pagination_json_string).unwrap();
    println!(
        "parse pagination_model from json string:{:?}", pagination_one_model
    );
}
