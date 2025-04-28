<template>
    <div class="container">
        <div class="row justify-content-center">
            <div class="col col-md-6 col-lg-4 col-xl-3 py-4">
                <main class="form-signin w-100 m-auto">
                    <form>
                        <h1 class="h3 fw-normal">Register account</h1>
                        <p class="text-body-secondary">Please fill in all details below.</p>

                        <div class="form-floating my-1">
                            <input type="text" class="form-control" name="first_name" placeholder="First name">
                            <label for="first_name">First name</label>
                        </div>

                        <div class="form-floating my-1">
                            <input type="text" class="form-control" name="last_name" placeholder="Last name">
                            <label for="last_name">Last name</label>
                        </div>

                        <div class="form-floating my-1">
                            <input type="text" class="form-control" name="email" placeholder="Email address">
                            <label for="email">Email</label>
                        </div>

                        <div class="form-floating my-1">
                            <input type="text" class="form-control" name="phone" placeholder="Phone number">
                            <label for="phone">Phone number</label>
                        </div>

                        <div class="form-floating my-1">
                            <input type="text" class="form-control" name="bsn" placeholder="BSN">
                            <label for="bsn">BSN</label>
                        </div>

                        <div class="form-floating my-1">
                            <input type="password" class="form-control" name="password" placeholder="Password">
                            <label for="password">Password</label>
                        </div>

                        <div class="form-floating my-1">
                            <input type="password" class="form-control" name="password_confirmation"
                                placeholder="Password confirmation">
                            <label for="password_confirmation">Password confirmation</label>
                        </div>

                        <button class="btn btn-primary w-100 py-2 my-3" type="submit">Confirm registration</button>
                    </form>
                </main>
            </div>
        </div>
    </div>
</template>
