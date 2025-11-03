use futures::FutureExt;

pub async fn catch_example() {
    let test_call = async {
        let _ = 100 / 0;
    };

    match test_call.catch_unwind().await {
        Err(e) => println!("task has exception = {}", e.downcast_ref::<&str>().unwrap()),
        _ => (),
    }
    println!("continue!!!")
}
