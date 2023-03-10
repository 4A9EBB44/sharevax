import axios from 'axios';
import {
	COUNTRIES,
	SUPPLIES,
	HARBORS,
	ACTIVE_DELIVERIES,
	PROGRESS_SIMULATION,
	SIMULATION_DAY,
	CREATE_EVENT,
	GET_EVENTS,
	FINISH_EVENT,
	RESET_SIMULATION,
	SUGGESTION,
	POST_SUGGESTION,
} from './endpoints';

export const fetchCountries = async function () {
	return await axios.get(COUNTRIES);
};

export const fetchSupplies = async function () {
	return await axios.get(SUPPLIES);
};

export const fetchHarbors = async function () {
	return await axios.get(HARBORS);
};

export const fetchActiveDeliveries = async function () {
	const url = ACTIVE_DELIVERIES;
	return await axios.get(url);
};

export const fetchSuggestions = async function (id) {
	return await axios.get(SUGGESTION(id));
};

export const postSuggestion = async function (suggestionId, countryId, approvalStatus, currentDate) {
	return await axios.post(POST_SUGGESTION, {suggestionId, countryId, approvalStatus, currentDate});
};

export const progressSimulation = async function () {
	return await axios.patch(PROGRESS_SIMULATION);
};

export const fetchSimulationDay = async function () {
	return await axios.get(SIMULATION_DAY);
};

export const resetSimulation = async function () {
	return await axios.patch(RESET_SIMULATION);
};

export const createEvent = async function (type, subject, remainingDaysToStart) {
	return await axios.post(CREATE_EVENT, {type, subject, remainingDaysToStart});
};

export const fetchEvents = async function () {
	return await axios.get(GET_EVENTS);
};

export const finishEvent = async function (eventId) {
	return await axios.post(FINISH_EVENT(eventId));
};
