package com.wan.s34476474.medtrack.data.network

import com.wan.s34476474.medtrack.data.drugs.DrugItem

// Wraps the OpenFDA API response and maps the returned drug results into a list of DrugItem objects
class ResponseModel(val results: List<DrugItem>)